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


