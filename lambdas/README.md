# Lambdas

These lambdas relate to automated tasks, usually run on a daily basis, which copy promotions data stored in the DynamoDB tables over to SalesForce and BigQuery, as well as backing up the DB tables themselves.

Five lambda functions are defined in the lambdas/src folder. They relate to eight lambdas in AWS,  in the memsub-promotions-lambdas application - link here: https://eu-west-1.console.aws.amazon.com/lambda/home?region=eu-west-1#/applications/memsub-promotions-lambdas 

We can also list just the Lambdas in AWS using this link: https://eu-west-1.console.aws.amazon.com/lambda/home?region=eu-west-1#/functions?fo=and&k0=functionName&o0=%3A&v0=MembershipSub

We can view the DynamoDB tables at this link (search on ‘prom’): https://eu-west-1.console.aws.amazon.com/dynamodbv2/home?region=eu-west-1#tables 


### MembershipSub-PromoCode-View-Dynamo-to-Data-Lake
A Lambda function to backup the PromoCode-View Dynamo table to the Data Lake.

The code dumps data to a csv in S3 https://github.com/guardian/memsub-promotions/blob/main/lambdas/src/MembershipSub-PromoCode-View-Dynamo-to-Data-Lake.js#L16
It runs on a daily schedule; for dev work we can run it manually and check that the csv gets updated. Always download the csv file from S3 before doing any dev/testing work - that way if anything does go wrong we can revert the code and reload the original file back into S3 to minimise damage.

A single version (PROD) of this lambda exists in AWS, and will rebuild automatically when a PR branch merges into `main`.

### MembershipSub-PromoCode-View-Dynamo-to-Salesforce
A Lambda function to replicate the MembershipSub-PromoCode-View-DEV Dynamo table to Salesforce.

This lambda sends data to salesforce on a daily schedule. We can just check the lambda's cloudwatch logs to confirm that it's happy - it logs all the requests it's making.

Three versions of this lambda (PROD, UAT, DEV) exist in AWS, each of which maps to thge equivalent environment in the SalesForce cloud. We can use the DEV version for testing. 

The lambda relies on SalesForce usernames/passwords which have been stored in AWS as encrypted environment variables.

Only the PROD will rebuild automatically when a PR branch merges into `main`.

### MembershipSub-Promotions-Scheduled-Dynamo-Backup
A Lambda function to trigger the backup of the Campaigns and Promotions table and delete any old backups (retention days is defined in the code).

For dev work, we can manually run it in AWS and check it worked from the Dynamo page in the AWS console.

A single version (PROD) of this lambda exists in AWS, and will rebuild automatically when a PR branch merges into `main`.

### MembershipSub-Promotions-to-PromoCode-View
An Amazon DynamoDB trigger that creates a view of data associated with a particular promo code for the data pipeline to collect.

The code writes updates from the Promotions table to the PromoCode-View table. 

During development work, we can test by creating a test promotion in the Promo Tool (this repo's frontend) in its DEV page and check that it gets written to PromoCode-View-DEV table.

Three versions of this lambda (PROD, UAT, DEV) exist in AWS. We can use the DEV version for testing. Only the PROD will rebuild automatically when a PR branch merges into `main`.

### MembershipSub-Reconstruct-PromoCode-View
This lambda appears to be for rebuilding the MembershipSub-PromoCode-View table. The intention is for it to be run manually if that is ever required - https://github.com/guardian/memsub-promotions/blob/7009715e905aa68de3b5c939ccc54f97b13e3c68/lambdas/src/MembershipSub-Reconstruct-PromoCode-View.js#L4


## Development
Because these are lambdas, we cannot develop and test on local devices. Instead code needs to be written locally and then all the lambda files in the lambdas/source folder (but not the folder itself, or any subfolders) need to be zipped and uploaded manually - using the AWS lambdas console - for testing.


## Deployment

The deploy is called MemSub::Membership Admin::Promotions Tool Lambda

Note that only the PROD versions of the lambdas will automatically rebuild. UAT and DEV versions need to be updated manually in the AWS lambdas console
