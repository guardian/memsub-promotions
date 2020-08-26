# Lambdas

This lambda is to update the view dynamo table from the main one.

## Deployment

The deploy is called MemSub::Membership Admin::Promotions Tool Lambda

teamcity will
1. run lambdas/build.sh
1. then upload the MembershipSub-Promotions-to-PromoCode-View-Lambda.zip to the Lambda function on S3 
1. refresh the package
