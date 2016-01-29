## 1.3.23

IMPROVEMENTS:

  - core *[DavDestination.java, DavMoveResource.java, DavCollectionResource.java, DavCmpEsImpl.java, etc.]*:
    MOVE method for following items are implemented. (Some restrictions apply.)
   * WebDav
   * Collection
   * Service
  - core *[UserSchemaODataProducer.java ]*:
    PUT methods to change the name of following items are implmented.
   * EntityType
   * Property
   * ComplexTypeProperty
   * AssociationEnd

## 1.3.22a

BACKWARD INCOMPATIBILITIES:

  - core *[DcCoreAuthnException.java]*:
     Response code for authentication failure with OAuth 2.0 (__auth endpoint) has been changed as follows.

    | Versions         | Response code & header      |
    | :-----------     | :-------------------------- |
    | Prior to V1.3.22 | 401 with/without authentication header depending on authentication type. |
    | V1.3.22          | 401 with header "WWW-Authenticate: xxxxx" |
    | From V1.3.22a    | Basic authentication: 400 with header "WWW-Authenticate: Basic".  Client authentication: See KNOWN ISSUES below. |


KNOWN ISSUES:

  - core :
    Response code for client authentication failure with OAuth 2.0 (__auth endpoint) should be 401 and include 
    "WWW-Authenticate" response header. However current version of personium.io returns response code 400 without 
    authenticate header due to compatibility for existing applications.


## 1.3.22

IMPROVEMENTS:

  - core *[EsQueryHandler.java]*:
    Implemented `ne` (not equal) operator for OData $filter query. List of supported operators and functions follows.

    | Operator | Description           | Example                                                                  | Note |
    | :------- | :-------------------  | :----------------------------------------------------------------------- | :--- |
    | eq       | Equal                 |  \$filter=itemKey eq 'searchValue'  <br/> \$filter=itemkey eq 10         |      |
    | ne       | Not equal             | $filter=itemKey ne 'searchValue'                                         |      |
    | gt       | Greater than          | $filter=itemKey gt 1000                                                  |      |
    | ge       | Greater than or equal | $filter=itemKey ge 1000                                                  |      |
    | lt       | Less than             | $filter=itemKey lt 1000                                                  |      |
    | le       | Less than or equal    | $filter=itemKey le 1000                                                  |      |
    | gt       | Greater than          | $filter=itemKey gt 1000                                                  |      |
    | and      | Logical and           | $filter=itemKey eq 'searchValue1' and itemKey2 eq 'searchValue2'         |      |
    | or       | Logical or            | $filter=itemKey eq 'searchValue1' or itemKey2 eq 'searchValue2'          |      |
    | ()       | Precedence grouping   | $filter=itemKey eq 'searchValue' or (itemKey gt 500 and itemKey lt 1500) |      |

    | Function    | Description        | Example                                       | Note                         |
    | :---------- | :----------------- | :-------------------------------------------- | :--------------------------- |
    | startswith  |                    | $filter=startswith(itemKey, 'searchValue')    | Null value is not supported. |
    | substringof |                    | $filter=substringof('searchValue1', itemKey1) | Null value is not supported. |

BUG FIXES:

  - core *[EsQueryHandler.java, DcOptionsQueryParser.java, DcCoreExceptoin.java]*:
    Unexpected result or error was retunred when unsupported operator or function is specified in query. Now returns Bad request (400).

  - core *[EsQueryHandler.java]*:
    No data was returned when searching with query that contains control codes as an operand. Fixed.

BACKWARD INCOMPATIBILITIES:

  - core *[EsQueryHandler.java, DcOptionsQueryParser.java, FilterConditionValidator.java, DcCoreExceptoin.java]*:
    Due to the above improvements and bug fixes, $filter behavior has been changed as follows:

    || When undefined property is specified as query operand. |
    |:--- |:----|
    | Prior to V1.3.22 | Nothing is Returned. |
    | From V1.3.22     | Bad Request(400) |
 
    || When the format of operand value is different from the type of property. |
    |:--- |:----|
    | Prior to V1.3.22 | If the operand value is castable to the type of assocaiated property, the operand is treated as valid.<br/>If not castable, retunrs Bad Request(400).  |
    | From V1.3.22     | Bad Request(400) |

    || When operand value is out of range for the type of property.|
    |:--- |:---- |
    | Prior to V1.3.22 | The operand value is treated as a valid operand, but may cause either unexpected result or error.|
    | From V1.3.22     | Bad Request(400) |

    || To search data including \\ (back-slash) |
    |:--- |:---- |
    | Prior to V1.3.22 | No escaping is required in query value.. |
    | From V1.3.22     | Escaping '\' (back-slash) required, such as '\\\\' |




## 1.3.21

BREAKING CHANGES:

  - core *[AccessConetxt.java, etc.]*:
    Supports Basic authentication (user, password) with Authorization header to allow access to the resource.

BACKWARD INCOMPATIBILITIES:

  - core *[BoxPrivilege.java, ODataSvcCollectionResource.java, ODataSvcSchemaResource.java]*:
    Added `alter-schema` privilege.  Prior to 1.3.21, OData schema can be changed with `write` privilege,  but from 1.3.21, `alter-schema` privilege is required to change the schema.

  - core *[BoxUrlResource.java]*:
    Changed response code of "Get Box URL" API from 302 to 200 to prevent redirection to the "Location URL" on some environment.

IMPROVEMENTS:

  - core *[DcCoreConfig.java, AuthUtils.java]*:
    Password salt was hard-coded and the same value was used for every personium runtime,  so that it could be a threat in terms of security. Now it can be specified with individual value in dc-config.properties.

  - core *[BinaryDataAccessor.java]*:
    Corrected file write operation to ensure that the data is flushed and synced to the storage device.

BUG FIXES:

  - core *[DcEngineSvcCollectionResource.java]*, engine *[DcResponse.java]*: 
    Status code 500 was returned when "Transfer-Encoding: chuncked" header was given on engine response. Fixed.

  - core *[AccessContext.java, DcCoreAuthzException.java, etc.]*:
    Authentication and authorization behavior is corrected to comply with HTTP RFC.

