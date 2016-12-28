/**
 * personium.io
 * Copyright 2014 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fujitsu.dc.engine.extension.support;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.engine.DcEngineException;

/**
 * Extension用の jarファイルのロード、Rhinoへの登録等を行うクラス.
 */
public class ExtensionJarLoader {

    /** ログオブジェクト. */
    private static Logger log = LoggerFactory.getLogger(ExtensionJarLoader.class);

    private static ExtensionJarLoader singleton = null;

    /** Extension用 jarファイルの格納パスの基底部分を定義しているシステムプロパティキー.
     * 実際のパスはこれに、"/dc-engine/extensions"が加えられる。 */
    public static final String ENGINE_EXTENSION_DIR_KEY = "io.personium.environment";
    /** 上記システムプロパティが指定されていない場合の、既定の Extension用 jarファイルの格納パス. */
    public static final String DEFAULT_EXTENSION_DIR = "/personium";

    private static final String JAR_SUFFIX = "jar";

    // 以下、内部変数
    private Path extensionJarDirectory = null;
    // ExtensionJarDirectoryの子/孫ディレクトリ内の jarファイルもロードするか否かを示すフラグ。
    private boolean searchDescendant = true;
    // jarファイルに置かれているクラスを置くためのクラスローダ
    private ClassLoader classloader = null;
    /**
     * JavaScript内でプロトタイプとして利用する実JavaクラスのSet.
     */
    private Set<Class<? extends Scriptable>> scriptableClassSet = null;

    /**
     * 既定のExtension用jarファイルディレクトリを参照するインスタンスを返却する.
     * @param parentCl 親クラスローダ
     * @param filter Extension用クラスフィルタ
     * @return ExtensionLoaderインスタンス
     * @throws IOException Extension読み込み中にエラーが発生
     * @throws DcEngineException Extension読み込み中にエラーが発生
     */
    public static ExtensionJarLoader getInstance(ClassLoader parentCl, ExtensionClassFilter filter)
            throws IOException, DcEngineException {
        if (null == singleton) {
            String extensionDir = System.getProperty(ENGINE_EXTENSION_DIR_KEY, DEFAULT_EXTENSION_DIR)
                + "/dc-engine/extensions";
            singleton = new ExtensionJarLoader(Paths.get(new File(extensionDir).toURI()), false, parentCl, filter);
        }
        return singleton;
    }

    /**
     * コンストラクタ.
     * PCSの一般用途としては利用すべきではなく、代わりに getInstance()を使用すること。
     * @param extJarDir Extension用jarファイルの格納ディレクトリ
     * @param searchDescend true: サブディレクトリも検索する, false: サブディレクトリは検索しない
     * @param parentCl 親クラスローダ
     * @param filter Extension用クラスフィルタ
     * @throws IOException Extension読み込み中にエラーが発生
     * @throws DcEngineException エラー
     */
    private ExtensionJarLoader(Path extJarDir, boolean searchDescend, ClassLoader parentCl,
            ExtensionClassFilter filter) throws IOException, DcEngineException {
        extensionJarDirectory = extJarDir;
        searchDescendant = searchDescend;
        List<URL> jarPaths = getJarPaths(extensionJarDirectory, searchDescendant);
        classloader = new URLClassLoader(jarPaths.toArray(new URL[]{}), parentCl);

        scriptableClassSet = loadPrototypeClassSet(jarPaths, filter);
    }

    /**
     * Extension用ディレクトリ内の jarファイルクラスをロードしたクラスローダを生成・返却する.
     * @return クラスローダ
     */
    public ClassLoader getClassLoader() {
        return classloader;
    }

    /**
     * JavaScript内でプロトタイプとして利用する実JavaクラスのSetを返却する.
     * @return プロトタイプとして利用する実JavaクラスのSet
     */
    public Set<Class<? extends Scriptable>> getPrototypeClassSet() {
        return scriptableClassSet;
    }

    /**
     * ExtensionJarDirectory内にある jarファイルのURLリストを返却する.
     * サフィックスは、小文字の "jar"のみを認識する。
     * @param extJarDir Extension用jarファイルの格納ディレクトリ
     * @param searchDescend true: サブディレクトリも検索する, false: サブディレクトリは検索しない
     * @return jarファイルの URLリスト.
     */
    private List<URL> getJarPaths(Path extJarDir, boolean searchDescend) throws DcEngineException {
        try {
            // 結果格納用リスト
            List<URL> uriList = new ArrayList<URL>();
            // jarファイルと同じ場所を含める。
            uriList.add(new URL("file", "", extJarDir.toFile().getAbsolutePath() + "/"));

            // jarファイルの検索
            File[] jarFiles = extJarDir.toFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (!pathname.exists() || !pathname.canRead() || pathname.isDirectory()) {
                        return false;
                    }
                    return FilenameUtils.isExtension(pathname.getName(), JAR_SUFFIX);
                }
            });

            if (null != jarFiles) {
                for (File jarFile : jarFiles) {
                    try {
                        uriList.add(new URL("file", "", jarFile.getCanonicalPath()));
                        log.info(
                            String.format("Info: Adding extension jar file %s to classloader.", jarFile.toURI()));
                    } catch (MalformedURLException e) {
                        // ############################################################################3
                        // ここに到達した場合、該当の jarファイルは読み込めないが、他の jarファイルの処理は継続する。
                        // 複数の Extensionが導入されている場合、問題となる Extensionを利用していない UserScriptまで
                        // 実行できなくなるのを防ぐため。
                        // 問題の Extensionにアクセスした場合、Script実行時のエラーとなる。
                        // ############################################################################3
                        log.info(
                           String.format("Warn: Some Extension jar file has malformed path. Ignoring: %s",
                                jarFile.toURI()));
                    } catch (IOException e) {
                        log.info(
                           String.format("Warn: Some Extension jar file has malformed path. Ignoring: %s",
                                jarFile.toURI()));
                    }
                }
            }

            // サブディレクトリの検索
            File[] subDirs = extJarDir.toFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.exists() && pathname.isDirectory() && pathname.canRead();
                }
            });

            if (null != subDirs) {
                for (File subDir : subDirs) {
                    // サブディレクトリ内 jarの追加
                    uriList.addAll(getJarPaths(subDir.toPath(), searchDescend));
                }
            }
            return uriList;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.info(String.format("Warn: Error occured while loading Extension: %s", e.getMessage()));
            throw new DcEngineException("Error occured while loading Extension.",
                    DcEngineException.STATUSCODE_SERVER_ERROR, e);
        } catch (Exception e) {
            log.info(String.format("Warn: Error occured while loading Extension: %s", e.getMessage()));
            throw new DcEngineException("Error occured while loading Extension.",
                    DcEngineException.STATUSCODE_SERVER_ERROR, e);
        }
    }

    /**
     * jarファイル内のエントリを検索し、JavaScriptへ公開するクラスの Setを返却する.
     * @param jarPaths  jarファイルのパスリスト
     * @param filter Extension用クラスフィルタ
     * @return Javascript内で使用するプロトタイプ名と、Javaクラスのマップ
     * @throws IOException マニフェストファイルが読み込めない/存在しない場合
     */
    @SuppressWarnings("unchecked")
    private Set<Class<? extends Scriptable>> loadPrototypeClassSet(List<URL> jarPaths,
            ExtensionClassFilter filter) throws IOException, DcEngineException {
        scriptableClassSet = new HashSet<Class<? extends Scriptable>>();

        for (URL jarUrl : jarPaths) {
            JarFile jar = null;
            try {
                File jarFile = new File(jarUrl.getPath());
                if (jarFile.isDirectory()) {
                    continue;
                }
                jar = new JarFile(jarFile);

                for (Enumeration<JarEntry> ent = jar.entries(); ent.hasMoreElements();) {
                    JarEntry entry = ent.nextElement();
                    String[] pathAndName = resolveJarEntry(entry.getName());
                    if (null == pathAndName) {
                        continue;
                    }
                    String entryPath = pathAndName[0];
                    String entryName = pathAndName[1];
                    if (null == entryPath || null == entryName) {
                        continue;
                    }
                    //  ※ jarエントリは、"/" がセパレータなので置き換える。
                    entryPath = entryPath.replaceAll("\\/", "\\.");
                    // このエントリが JavaScriptに公開されるか否かを filterに問い合わせる。
                    if (filter.accept(entryPath,  entryName)) {
                        String className = entryPath + "." + entryName;
                        try {
                            Class<?> cl = classloader.loadClass(className);
                            if (ScriptableObject.class.isAssignableFrom(cl) || Scriptable.class.isAssignableFrom(cl)) {
                                scriptableClassSet.add((Class<? extends Scriptable>) cl);
                                log.info(String.format(
                                    "Info: Extension class %s is revealed to JavaScript.", className));
                                // OK.
                                continue;
                            }
                            // ScriptableObject/Scriptableを継承していないため、JavaScriptに公開できない。
                            log.info(String.format(
                                "Info: Extension class %s is not derived from "
                                    + "ScriptableObject class or does not implment Scriptable interface. Ignored.",
                                    className));
                        } catch (ClassNotFoundException e) {
                            log.warn(
                               String.format("Warn: Extension class %s is not found in classLoader: %s",
                                       className, e.getMessage()), e);
                        } catch (NoClassDefFoundError e) {
                            log.warn(
                                String.format("Warn: Extension class %s is not found in classLoader: %s",
                                            className, e.getMessage()), e);
                        } catch (Exception e) {
                            log.warn(String.format(
                                 "Warn: Extension class %s cannot be loaded into classLoader: %s "
                                 + "or the jar content is invalid.",
                                 className, e.getMessage()), e);
                        }
                    }
                }
            } catch (RuntimeException e) {
                log.warn(String.format(
                        "Warn: Failed to handle Extension jar file %s: %s", jarUrl.toString(), e.getMessage()), e);
            } catch (Exception e) {
                log.warn(String.format(
                        "Warn: Failed to handle Extension jar file %s: %s", jarUrl.toString(), e.getMessage()), e);
                continue;
            } finally {
                IOUtils.closeQuietly(jar);
            }
        }
        return scriptableClassSet;
    }

    /**
     * Jarファイル内のエントリから、パス部分とクラス名部分とを分離する.
     * @param jarEntryStr Jarファイル内のエントリパス
     * @return パス部分 と クラス名部分の２つの要素を持つ文字列配列
     */
    private String[] resolveJarEntry(String jarEntryStr) {
        if (null == jarEntryStr || jarEntryStr.isEmpty()) {
            return null;
        }
        int dotClassPosition = jarEntryStr.lastIndexOf(".class");
        if (0 < dotClassPosition) {
            jarEntryStr = jarEntryStr.substring(0, jarEntryStr.lastIndexOf(".class"));
            int lastSeparator = jarEntryStr.lastIndexOf('/');
            if (-1 != lastSeparator) {
                return new String[] {
                        jarEntryStr.substring(0, lastSeparator),
                        jarEntryStr.substring(lastSeparator + 1) };
            } else {
                return new String[] {"", jarEntryStr};
            }
        }
        return null;
    }
}

