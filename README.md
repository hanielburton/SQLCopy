# SQLCopy
Copies data from one SQL source to another.

Works with Oracle and MS SQL databases. MySQL should also work with correct drivers and JDBC string. 

Best used with identical source and target tables or views. Preferably from view to table so any escaped characters and select criteria can be coded into the source view. 

Everything is read as string values and inserted as such, so it does not play well with datetime/timestamp values or BLOBS/CLOBS, yet. 

Future feature releases (in no particular order):

-Logging to text file
-Export to CSV on local filesystem as target
-Export to CSV on remote SFTP target
-Smarter handling of column types so dates, BLOBs, and other fields are supported
-Possibility to allow error checking and skipping error rows
-More user friendly config file formats
-Ability to configure options through CLI and export pre-formatted config file
