# Connector Scim2

# 1	Overview
ConnID Connector to support [System for Cross-domain Identity Management (SCIM2)](https://www.rfc-editor.org/rfc/rfc7642.html) 
API for Identify and Access Management (IAM) of Remote Services

This open source connector for the SCIM2 API uses the [ConnId Framework](https://connid.tirasa.net/) 
for integration with Identity and Access Management (IAM) systems such as [Evolveum Midpoint](https://evolveum.com/midpoint/).

The software leverages the Connector Base Framework located at https://github.com/ExclamationLabs/connector-base

This software is Copyright 2024 Exclamation Graphics. Licensed under the Apache License, Version 2.0.

# 2	Features

The Scim2 Connector has the following features:

* The connector configuration is specified in the user interface.
* The connector supports Users and Groups.
* A User can be associated with one or more Groups.
* The connector can Create, Update, Delete, and Search Users.
* The connector can Create, Update, Delete, and Search Groups.
* The connector supports automatic pagination for User and Group objects.
* The connector currently supports standard, enterprise, AWS, and Slack.

*Note:* Dynamic schema support is currently not implemented.  It may be implemented in a future release.

# Connector Configuration

The actual method of configuring a connector is largely dependent on the interface(s) provided by your Identity and
Access management system. Midpoint provides a convenient user interface method to enter these values. If configuration
properties are being read in from a property file, you may also need to know the name of the property.

## SCIM2 Configuration Options

<table>
    <thead>
        <tr>
        <td><strong>Property Name</strong></td>
        <td><strong>Required</strong></td> 
        <td><strong>Description/Notes</strong></td>
        </tr>
    </thead>
    <tbody>
      <tr>
       <td>Service URL
       </td>
       <td>Yes
       </td>
       <td>The base URL of the SCIM2 endpoint. 
       </td>
      </tr>
      <tr>
       <td>IO Error Retries
       </td>
       <td>No
       </td>
       <td>Number of retries that will be attempted when an IO error occurs. Default is 5.
       </td>
      </tr>
      <tr>
      <tr>
       <td>Token
       </td>
       <td>Yes
       </td>
       <td>The token used to authenticate with the SCIM2 endpoint. This is usually a bearer token or an OAuth2 token. The connector will use this token to authenticate all requests to the SCIM2 endpoint.
       </td>
      </tr>
      <tr>
       <td>Deep Get Enabled
       </td>
       <td>No
       </td>
       <td>When a search operation is executed and this value is <strong>true,</strong> the connector will make an individual call to download each User or Group returned. The value should be set to false since a standard SCIM2 service should return all attributes of each record.
       </td>
      </tr>
      <tr>
       <td>Deep Import Enabled
       </td>
       <td>No
       </td>
       <td>When an import operation is executed and this value is <strong>true</strong> the connector will attempt to download all attributes for each individual record returned. The option should be set to <strong>false</strong> since the SCIM2 connector should return all attributes on the search. 
       </td>
      </tr>
      <tr>
       <td>Import Batch Size
       </td>
       <td>No
       </td>
       <td>The default number of records to retrieve per page. Import operations will be invoked using the given batch size when it is supplied. Since the SCIM2 API supports paging you can import records one batch/page at a time instead of all at once.
       </td>
      </tr>
      <tr>
       <td>Pagination Enabled
       </td>
       <td>No
       </td>
       <td>The SCIM2 Connector supports pagination on User and Group objects. This option should be set to true.
       </td>
      </tr>
      <tr>
       <td>Duplicate Record Returns Id
       </td>
       <td>No
       </td>
       <td>When a create is attempted and an AlreadyExistsException is generated, the adapter shall attempt to return the id of the existing record matching the specified userName. 
       </td>
      </tr>  
      <tr>
       <td>Enable Standard Schema
       </td>
       <td>Yes
       </td>
       <td>Uses prebuilt java objects based on the stand schema. Default is <strong>false</strong>.
       </td>
      </tr>
      <tr>
       <td>Enable Enterprise User
       </td>
       <td>Yes
       </td>
       <td>Extend the user schema with enterprise attributes. Default is <strong>false</strong>.
       </td>
      </tr>
      <tr>
       <td>Enable AWS Schema
       </td>
       <td>Yes
       </td>
       <td>Use a pre-built java classes as defined for AWS,
    as specified <a href="https://docs.aws.amazon.com/singlesignon/latest/developerguide/what-is-scim.html">here</a>.
        Default is <strong>false</strong>.
       </td>
      </tr>
      <tr>
       <td>Enable Slack Schema
       </td>
       <td>Yes
       </td>
       <td>Use prebuilt java classes as define for Slack as specified 
    <a href="https://api.slack.com/admins/scim2">here</a>. Default is <strong>false</strong>.
       </td>
      </tr>
      <tr>
       <td>Enable Dynamic Schema
       </td>
       <td>Yes
       </td>
       <td>Use the Resource Type and/or the Schema defined above for dynamic operations. Default is <strong>false</strong>.
       </td>
      </tr>
      <tr>
       <td>Users Endpoint URL
       </td>
       <td>Yes
       </td>
       <td>Discovered from the resource type or entered manually. Default is <strong>/Users</strong>.
       </td>
      </tr>
     <tr>
       <td>Groups Endpoint URL
       </td>
       <td>Yes
       </td>
       <td>Discovered from the resource type or entered manually. Default is <strong>/Groups</strong>.
       </td>
      </tr>
      <tr>
       <td>UserSchemaIdList
       </td>
       <td>No
       </td>
       <td>
    A list of user schemas that define a user.<br/>
    This is discoverable from the Resource Type URL, JSON, or by one of the prebuilt java classes<br/>
    Usually:
    <ul>
    <li>urn:ietf:params:scim:schemas:core:2.0:User</li>
    <li>urn:ietf:params:scim:schemas:extension:enterprise:2.0:User</li>
    </ul>
       </td>
      </tr>
      <tr>
       <td>GroupSchemaIDList
       </td>
       <td>No
       </td>
       <td>A list of Group schemas that define a Group.<br/>
    This is discoverable from the Resource Type URL or JSON. or by one of the prebuilt java classes.  
    Usually:
    <ul>
    <li>urn:ietf:params:scim:schemas:core:2.0:Group+etc</li>
    </ul>
       </td>
      </tr>
    </tbody>
</table>


## Configuration properties

The following property names can be used when integrating with a Connid system that uses a configuration properties file. It is also used to perform unit tests in the code base.  \
See src/test/resources/__bcon__development__exclamation_labs__scim2.properties for an example.


<table>
    <thead>
      <tr>
       <td><strong>Item</strong>
       </td>
       <td><strong>Type</strong>
       </td>
       <td><strong>Property Name</strong>
       </td>
      </tr>
    </thead>
    <tbody>
      <tr>
       <td>Service URL
       </td>
       <td>String
       </td>
       <td>service.serviceUrl
       </td>
      </tr>
      <tr>
       <td>IO Error Retries
       </td>
       <td>Integer
       </td>
       <td>rest.ioErrorRetries
       </td>
      </tr>
        <tr>
            <td>Token</td>
            <td>String</td>
            <td>security.authenticator.directAccessToken.token</td>
        </tr>
      <tr>
       <td>Deep Get Enabled
       </td>
       <td>Boolean
       </td>
       <td>results.deepGet
       </td>
      </tr>
      <tr>
       <td>Deep Import Enabled
       </td>
       <td>Boolean
       </td>
       <td>results.deepImport
       </td>
      </tr>
      <tr>
       <td>Import Batch Size
       </td>
       <td>Integer
       </td>
       <td>results.importBatchSize
       </td>
      </tr>
      <tr>
       <td>Pagination Enabled
       </td>
       <td>Boolean
       </td>
       <td>results.pagination
       </td>
      </tr>
      <tr>
       <td>Duplicate Record Returns Id
       </td>
       <td>Boolean
       </td>
       <td>service.duplicateErrorReturnsId
       </td>
      </tr> 
      <tr>
       <td>Enable Standard Schema
       </td>
       <td>Boolean
       </td>
       <td>custom.enableStandardSchema
       </td>
      </tr>  
      <tr>
       <td>Enable Enterprise User
       </td>
       <td>Boolean
       </td>
       <td>custom.enableEnterpriseUser
       </td>
      </tr>  
      <tr>
       <td>Enable AWS Schema
       </td>
       <td>Boolean
       </td>
       <td>custom.enableAWSSchema
       </td>
      </tr>  
      <tr>
       <td>Enable Slack Schema
       </td>
       <td>Boolean
       </td>
       <td>custom.enableSlackSchema
       </td>
      </tr>  
      <tr>
       <td>Enable Dynamic Schema
       </td>
       <td>Boolean
       </td>
       <td>custom.enableDynamicSchema
       </td>
      </tr>  
      <tr>
       <td>Users Endpoint URL
       </td>
       <td>String
       </td>
       <td>custom.usersEndpointUrl
       </td>
      </tr>  
      <tr>
       <td>Groups Endpoint URL
       </td>
       <td>String
       </td>
       <td>custom.groupsEndpointUrl
       </td>
      </tr>  
      <tr>
       <td>User SchemaId List
       </td>
       <td>String Array
       </td>
       <td>custom.userSchemaIdList
       </td>
      </tr>  
      <tr>
       <td>Group SchemaId List
       </td>
       <td>String Array
       </td>
       <td>custom.groupSchemaIdList
       </td>
      </tr>
    </tbody>
</table> 

# 5	Connector Operations

The SCIM2 connector implements the following connId SPI operations: 

* **SchemaOp** - Allows the Connector to describe which types of objects the Connector manages on the target resource. This includes the options supported for each type of object.
* **TestOp** - Allows testing of the resource configuration to verify that the target environment is available (ie. validate the connection to the SCIM2 target system).
* **SearchOp** - Allows the connector to search the SCIM2 target system for resource objects (most commonly users and groups).
* **CreateOp** - Allows the connector to create SCIM2 Users and Groups.
* **DeleteOp** - Allows the connector to delete SCIM2 Users and Groups.
* **UpdateDeltaOp** - Allows the connector to update SCIM2 Users and Groups.  Both PUT (full replacement) and PATCH (partial update) are supported.
* **DiscoverConfigurationOp** - Allows the connector to suggest configuration values in the user interface at creation time.


## Deep Get Explained

**Deep Get** signifies that a separate HTTP GET is required and automatically executed for each individual resource after a GET request for a list of resources (with or without filters or pagination) is executed.  The SCIM2 standard for listing resources has all detail needed, so **deep get** is not needed for SCIM2 connector and should always be set to *false*.

## Deep Import Explained

**Deep Import** signifies that a separate HTTP GET is required and automatically executed for each individual resource after a GET request for a *full list* of resources is executed.  The SCIM2 standard for listing resources has all detail needed, so **deep import** is not needed for SCIM2 connector and should always be set to *false*.
 

## Duplicate Record Returns Id Explained

The duplicate record returns Id configuration option is invoked when an HTTP POST request, used to create a record, returns HTTP 409 (Conflict). This typically indicates that the record we are attempting to create already exists. When this option is true the connector will attempt to get the record by name and return the record’s ID value to the caller. In this way a record can be seamlessly imported when it already exists on the target server. Unfortunately the SCIM2 API does not return HTTP 409 and instead returns HTTP 412. Because this is the case the connector will always do a lookup for an existing object type before creating the type.

# 6 Connector Schema

The SCIM2 connector supports User and Group objects.  The full schemas for these objects, and for all complex types can be found in the `schema_doc` directory of this project.

## SCIM2User Object

<table style="border: 1px black;">
	<thead style="text-align: left">
		<tr>
			<th>name</th>
			<th>type</th>
			<th>description</th>
		</tr>
	</thead>
	<tbody style="vertical-align: top">
		<tr>
			<td>userName</td>
			<td>string</td>
			<td>Unique identifier for the User, typically used by the user to directly authenticate to the service provider. Each User MUST include a non-empty userName value. This identifier MUST be unique across the service provider's entire set of Users. REQUIRED.</td>
		</tr>
		<tr>
			<td>name</td>
			<td>complex</td>
			<td>The components of the user's real name. Providers MAY return just the full name as a single string in the formatted sub-attribute, or they MAY return just the individual component attributes using the other sub-attributes, or they MAY return both. If both variants are returned, they SHOULD be describing the same name, with the formatted name indicating how the component attributes should be combined.</td>
		</tr>
		<tr>
			<td>displayName</td>
			<td>string</td>
			<td>The name of the User, suitable for display to end-users. The name SHOULD be the full name of the User being described, if known.</td>
		</tr>
		<tr>
			<td>nickName</td>
			<td>string</td>
			<td>The casual way to address the user in real life, e.g., 'Bob' or 'Bobby' instead of 'Robert'. This attribute SHOULD NOT be used to represent a User's username (e.g., 'bjensen' or 'mpepperidge').</td>
		</tr>
		<tr>
			<td>profileUrl</td>
			<td>reference</td>
			<td>A fully qualified URL pointing to a page representing the User's online profile.</td>
		</tr>
		<tr>
			<td>title</td>
			<td>string</td>
			<td>The user's title, such as "Vice President."</td>
		</tr>
		<tr>
			<td>userType</td>
			<td>string</td>
			<td>Used to identify the relationship between the organization and the user. Typical values used might be 'Contractor', 'Employee', 'Intern', 'Temp', 'External', and 'Unknown', but any value may be used.</td>
		</tr>
		<tr>
			<td>preferredLanguage</td>
			<td>string</td>
			<td>Indicates the User's preferred written or spoken language. Generally used for selecting a localized user interface; e.g., 'en_US' specifies the language English and country US.</td>
		</tr>
		<tr>
			<td>locale</td>
			<td>string</td>
			<td>Used to indicate the User's default location for purposes of localizing items such as currency, date time format, or numerical representations.</td>
		</tr>
		<tr>
			<td>timezone</td>
			<td>string</td>
			<td>The User's time zone in the 'Olson' time zone database format, e.g., 'America/Los_Angeles'.</td>
		</tr>
		<tr>
			<td>active</td>
			<td>boolean</td>
			<td>A Boolean value indicating the User's administrative status.</td>
		</tr>
		<tr>
			<td>password</td>
			<td>string</td>
			<td>The User's cleartext password. This attribute is intended to be used as a means to specify an initial password when creating a new User or to reset an existing User's password.</td>
		</tr>
		<tr>
			<td>emails</td>
			<td>complex</td>
			<td>Email addresses for the user. The value SHOULD be canonicalized by the service provider, e.g., 'bjensen@example.com' instead of 'bjensen@EXAMPLE.COM'. Canonical type values of 'work', 'home', and 'other'.</td>
		</tr>
		<tr>
			<td>phoneNumbers</td>
			<td>complex</td>
			<td>Phone numbers for the User. The value SHOULD be canonicalized by the service provider according to the format specified in RFC 3966, e.g., 'tel:+1-201-555-0123'. Canonical type values of 'work', 'home', 'mobile', 'fax', 'pager', and 'other'.</td>
		</tr>
		<tr>
			<td>ims</td>
			<td>complex</td>
			<td>Instant messaging addresses for the User.</td>
		</tr>
		<tr>
			<td>photos</td>
			<td>complex</td>
			<td>URLs of photos of the User.</td>
		</tr>
		<tr>
			<td>addresses</td>
			<td>complex</td>
			<td>A physical mailing address for this User. Canonical type values of 'work', 'home', and 'other'. This attribute is a complex type with the following sub-attributes.</td>
		</tr>
		<tr>
			<td>groups</td>
			<td>complex</td>
			<td>A list of groups to which the user belongs, either through direct membership, through nested groups, or dynamically calculated.</td>
		</tr>
		<tr>
			<td>groups_value</td>
			<td>string</td>
			<td>A list of groupId to which the user belongs as multivalued string. This is the value attribute of the groups complex type</td>
		</tr>
		<tr>
			<td>entitlements</td>
			<td>complex</td>
			<td>A list of entitlements for the User that represent a thing the User has.</td>
		</tr>
		<tr>
			<td>roles</td>
			<td>complex</td>
			<td>A list of roles for the User that collectively represent who the User is, e.g., 'Student', 'Faculty'.</td>
		</tr>
		<tr>
			<td>x509Certificates</td>
			<td>complex</td>
			<td>A list of certificates issued to the User.</td>
		</tr>
	</tbody>
</table>

* ConnId UID -> SCIM2 id
* ConnId Name -> SCIM2 userName

## SCIM2User Object (Slack)

In addition to all of the above fields, the SCIM2User object for Slack also includes additional fields
(per `urn:ietf:params:scim:schemas:extension:slack:guest:2.0:User` schema):

<table style="border: 1px black;">
	<thead style="border: 1px black;">
		<tr>
			<th>name</th>
			<th>type</th>
			<th>description</th>
		</tr>
	</thead>
	<tbody style="vertical-align: top">
		<tr>
			<td>guestInfo</td>
			<td>complex</td>
			<td>Guest information for the user
                <h4>Elements:</h4>
                <ul>
                    <li>channels (list of Slack channels user belongs to (value/display fields for each)</li>
                    <li>expiration</li>
                    <li>type</li>
                </ul>
            </td>
		</tr>
		<tr>
			<td>profileInfo</td>
			<td>complex</td>
			<td>Profile information for the user
                <h4>Elements:</h4>
                <ul>
                    <li>startDate</li>
                </ul>
            </td>
		</tr>
	</tbody>
</table>

## SCIM2Group Object


<table>
  <tr>
   <td><strong>name</strong>
   </td>
   <td><strong>type</strong>
   </td>
   <td><strong>multivalued</strong>
   </td>
   <td><strong>description</strong>
   </td>
  </tr>
  <tr>
   <td>id
   </td>
   <td>string
   </td>
   <td>no
   </td>
   <td>The identifier of the Group.
   </td>
  </tr>
  <tr>
   <td>displayName
   </td>
   <td>string
   </td>
   <td>no
   </td>
   <td>A human-readable name, primarily used for display purposes.
   </td>
  </tr>
  <tr>
   <td>externalId
   </td>
   <td>string
   </td>
   <td>no
   </td>
   <td>A String that is an identifier for the resource as defined by the provisioning client.t'.
   </td>
  </tr>
  <tr>
   <td>members
   </td>
   <td>JSON String
   </td>
   <td>yes
   </td>
   <td>A list of members of the Group as a JSON array. 
   </td>
  </tr>
  <tr>
   <td>members_value
   </td>
   <td>string
   </td>
   <td>yes
   </td>
   <td>A list Group Identifiers for members of this Group.
   </td>
  </tr>
</table>

* ConnId UID -> SCIM2 id
* ConnId Name -> SCIM2 displayName

In a SCIM2 Group the members attribute is a multivalued string containing a map of members key/value pairs in JSON format. The following shows the expected return data for member:


```
{
"value": "9067729b3d-94f1e0b3-c394-48d5-8ab1-2c122a167074",
"$ref": "../Users/9067729b3d-94f1e0b3-c394-48d5-8ab1-2c122a167074",
"type": "User"
}
```

# 7 Connector Query Capabilities

The SCIM2 Connector supports the following Query Operations


## SCIM2User 

* Get User by id
* Get User by Name (userName)
* Get list of Users using pagination (page number and page size)
* Get all Users
* Create a new user (id is generated by the SCIM2 server and returned)
* Update a user by id (full replacement)
* Delete a user by id 

## SCIM2Group 

* Get Group by id
* Get Group by name (displayName)
* Get list of Groups using pagination (page number and page size)
* Get all Groups
* Create a new Group (id is generated by the SCIM2 server and returned)
* Update a Group by id (full replacement)
* Delete a Group by id

# References

1. [RFC 7642 SCIM2 Definitions, Overview, Concepts, and Requirements](https://datatracker.ietf.org/doc/html/rfc7642)
2. [RFC 7643 SCIM2 Core Schema](https://datatracker.ietf.org/doc/html/rfc7643)
3. [RFC 7644 SCIM2 Protocol](https://datatracker.ietf.org/doc/html/rfc7644)
4. [Amazon Web Services SCIM Implementation Guide](https://docs.aws.amazon.com/singlesignon/latest/developerguide/what-is-scim.html)
5. [Slack API Provisioning with SCIM 2.0](https://api.slack.com/admins/scim2)
6. [Zoom SCIM2 API](https://developers.zoom.us/docs/api/rest/reference/scim-api/methods/#overview)
7. [SalesForce SCIM2 Implementation](https://help.salesforce.com/s/articleView?id=sf.identity_scim_overview.htm&type=5)
8. [Fast Fed SCIM2 Interoperability Requirements](https://openid.net/specs/fastfed-scim-1_0-02.html#rfc.section.4)

