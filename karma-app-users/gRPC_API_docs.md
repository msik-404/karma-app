# Protocol Documentation
<a name="top"></a>

## Table of Contents

- [karma_app_users.proto](#karma_app_users-proto)
    - [CreateUserRequest](#karmaappusers-CreateUserRequest)
    - [CredentialsRequest](#karmaappusers-CredentialsRequest)
    - [CredentialsResponse](#karmaappusers-CredentialsResponse)
    - [UpdateUserRequest](#karmaappusers-UpdateUserRequest)
    - [UserIdRequest](#karmaappusers-UserIdRequest)
    - [UserRoleResponse](#karmaappusers-UserRoleResponse)
    - [UsernameResponse](#karmaappusers-UsernameResponse)
    - [UsernamesRequest](#karmaappusers-UsernamesRequest)
    - [UsernamesResponse](#karmaappusers-UsernamesResponse)
  
    - [UserRole](#karmaappusers-UserRole)
  
    - [Users](#karmaappusers-Users)
  
- [mongo_object_id.proto](#mongo_object_id-proto)
    - [ProtoObjectId](#protomongo-ProtoObjectId)
  
- [Scalar Value Types](#scalar-value-types)



<a name="karma_app_users-proto"></a>
<p align="right"><a href="#top">Top</a></p>

## karma_app_users.proto



<a name="karmaappusers-CreateUserRequest"></a>

### CreateUserRequest
Represents request for creating new user account.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| firstName | [string](#string) | optional | Optional user&#39;s firstname. |
| lastName | [string](#string) | optional | Optional user&#39;s lastName |
| username | [string](#string) | optional | Required unique user&#39;s username |
| email | [string](#string) | optional | Required unique user&#39;s email. Email must be in valid format. |
| password | [string](#string) | optional | Required user&#39;s password. Should already be hashed and salted, for example in Bcrypt. |
| role | [UserRole](#karmaappusers-UserRole) | optional | Required user&#39;s role. |






<a name="karmaappusers-CredentialsRequest"></a>

### CredentialsRequest
Represents request for fetching data required for authentication.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| email | [string](#string) | optional | Requested email which should be in valid format. |






<a name="karmaappusers-CredentialsResponse"></a>

### CredentialsResponse
Represents credentials required to perform authentication of a user.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| userId | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Id of the user. |
| password | [string](#string) | optional | Hashed and salted password of the user. |
| role | [UserRole](#karmaappusers-UserRole) | optional | Role of the user. |






<a name="karmaappusers-UpdateUserRequest"></a>

### UpdateUserRequest
Represents request for updating existing user account.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| userId | [protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | optional | Requested id of user account to be updated. |
| firstName | [string](#string) | optional | Optional new firstName value. |
| lastName | [string](#string) | optional | Optional new lastName value. |
| username | [string](#string) | optional | Optional unique new username value. |
| email | [string](#string) | optional | Optional unique new email value in valid format. |
| password | [string](#string) | optional | Optional new password, which should already by hashed and salted, for example in Bcrypt. |
| role | [UserRole](#karmaappusers-UserRole) | optional | Optional new role. |






<a name="karmaappusers-UserIdRequest"></a>

### UserIdRequest
Represents request for user&#39;s id.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| username | [string](#string) | optional | Required username of the user. |






<a name="karmaappusers-UserRoleResponse"></a>

### UserRoleResponse
Represents response with user&#39;s role.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| role | [UserRole](#karmaappusers-UserRole) | optional | Required role of the user. |






<a name="karmaappusers-UsernameResponse"></a>

### UsernameResponse
Represents response with user&#39;s username.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| username | [string](#string) | optional | Required username of the user. |






<a name="karmaappusers-UsernamesRequest"></a>

### UsernamesRequest
Represents request for list of usernames.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| userIdHexStrings | [string](#string) | repeated | List of userIds in string form. Each must be unique and have 24 chars. List should contain at least one userId. |






<a name="karmaappusers-UsernamesResponse"></a>

### UsernamesResponse
Represents response with usernames returned in the same order as ids in request.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| usernames | [string](#string) | repeated | List of usernames. If given username was not found, empty string is in it&#39;s place. |





 


<a name="karmaappusers-UserRole"></a>

### UserRole
Represents all accepted roles of a user.

| Name | Number | Description |
| ---- | ------ | ----------- |
| ROLE_USER | 0 | user role. |
| ROLE_MOD | 1 | mod role. |
| ROLE_ADMIN | 2 | admin role |


 

 


<a name="karmaappusers-Users"></a>

### Users
Service for finding, creating and updating users. It also returns data required for authorization.

| Method Name | Request Type | Response Type | Description |
| ----------- | ------------ | ------------- | ------------|
| createUser | [CreateUserRequest](#karmaappusers-CreateUserRequest) | [.google.protobuf.Empty](#google-protobuf-Empty) | Used for creating new user account. Returns encoded UnsupportedRoleException, DuplicateUsernameException, DuplicateEmailException, DuplicateUnexpectedFieldException on failure. |
| updateUser | [UpdateUserRequest](#karmaappusers-UpdateUserRequest) | [.google.protobuf.Empty](#google-protobuf-Empty) | Used for updating already existing user account. Only set fields will get updated. Returns encoded UserNotFoundException, UnsupportedRoleException, DuplicateUsernameException, DuplicateEmailException, DuplicateUnexpectedFieldException on failure. |
| findCredentials | [CredentialsRequest](#karmaappusers-CredentialsRequest) | [CredentialsResponse](#karmaappusers-CredentialsResponse) | Used for fetching credentials for authentication. Returns encoded UnsupportedRoleException, UserNotFoundException on failure. |
| findUserRole | [.protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | [UserRoleResponse](#karmaappusers-UserRoleResponse) | Used for fetching user&#39;s role of a given user by userId. Returns encoded UserNotFoundException on failure. |
| findUsername | [.protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | [UsernameResponse](#karmaappusers-UsernameResponse) | Used for fetching user&#39;s username of a given user by userId. Returns encoded UserNotFoundException on failure. |
| findUsernames | [UsernamesRequest](#karmaappusers-UsernamesRequest) | [UsernamesResponse](#karmaappusers-UsernamesResponse) | Used for fetching list of usernames by list of userIds. |
| findUserId | [UserIdRequest](#karmaappusers-UserIdRequest) | [.protomongo.ProtoObjectId](#protomongo-ProtoObjectId) | Used for fetching userId of a given user by username. Returns encoded UserNotFoundException on failure. |

 



<a name="mongo_object_id-proto"></a>
<p align="right"><a href="#top">Top</a></p>

## mongo_object_id.proto



<a name="protomongo-ProtoObjectId"></a>

### ProtoObjectId
Represents a unique document identifier in MongoDB.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| hexString | [string](#string) | optional | Required, unique string of exactly 24 characters. |





 

 

 

 



## Scalar Value Types

| .proto Type | Notes | C++ | Java | Python | Go | C# | PHP | Ruby |
| ----------- | ----- | --- | ---- | ------ | -- | -- | --- | ---- |
| <a name="double" /> double |  | double | double | float | float64 | double | float | Float |
| <a name="float" /> float |  | float | float | float | float32 | float | float | Float |
| <a name="int32" /> int32 | Uses variable-length encoding. Inefficient for encoding negative numbers – if your field is likely to have negative values, use sint32 instead. | int32 | int | int | int32 | int | integer | Bignum or Fixnum (as required) |
| <a name="int64" /> int64 | Uses variable-length encoding. Inefficient for encoding negative numbers – if your field is likely to have negative values, use sint64 instead. | int64 | long | int/long | int64 | long | integer/string | Bignum |
| <a name="uint32" /> uint32 | Uses variable-length encoding. | uint32 | int | int/long | uint32 | uint | integer | Bignum or Fixnum (as required) |
| <a name="uint64" /> uint64 | Uses variable-length encoding. | uint64 | long | int/long | uint64 | ulong | integer/string | Bignum or Fixnum (as required) |
| <a name="sint32" /> sint32 | Uses variable-length encoding. Signed int value. These more efficiently encode negative numbers than regular int32s. | int32 | int | int | int32 | int | integer | Bignum or Fixnum (as required) |
| <a name="sint64" /> sint64 | Uses variable-length encoding. Signed int value. These more efficiently encode negative numbers than regular int64s. | int64 | long | int/long | int64 | long | integer/string | Bignum |
| <a name="fixed32" /> fixed32 | Always four bytes. More efficient than uint32 if values are often greater than 2^28. | uint32 | int | int | uint32 | uint | integer | Bignum or Fixnum (as required) |
| <a name="fixed64" /> fixed64 | Always eight bytes. More efficient than uint64 if values are often greater than 2^56. | uint64 | long | int/long | uint64 | ulong | integer/string | Bignum |
| <a name="sfixed32" /> sfixed32 | Always four bytes. | int32 | int | int | int32 | int | integer | Bignum or Fixnum (as required) |
| <a name="sfixed64" /> sfixed64 | Always eight bytes. | int64 | long | int/long | int64 | long | integer/string | Bignum |
| <a name="bool" /> bool |  | bool | boolean | boolean | bool | bool | boolean | TrueClass/FalseClass |
| <a name="string" /> string | A string must always contain UTF-8 encoded or 7-bit ASCII text. | string | String | str/unicode | string | string | string | String (UTF-8) |
| <a name="bytes" /> bytes | May contain any arbitrary sequence of bytes. | string | ByteString | str | []byte | ByteString | string | String (ASCII-8BIT) |

