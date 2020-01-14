Subscription promotions tool
====================

### NGinx
1. `cd nginx`
1. Install dependencies `brew bundle`
2. Run `./setup.sh`

## General Setup

1. Go to project root
1. Add the following to your hosts file in `/etc/hosts`

   ```
   127.0.0.1   promo.thegulocal.com
   ```
1. Change the ownership of the 'gu' directory under 'etc' to current user.
   `$ sudo -i chown -R {username} /etc/gu`

1. Setup AWS credentials with [Janus](https://janus.gutools.co.uk/).

   In `~/.aws/config` add the following:

   ```
   [default]
   region = eu-west-1
   ```

1. Download our private keys from the gu-promotions-tool-private S3 bucket. If you have the AWS CLI set up you can run:
   ```
   aws s3 cp s3://gu-reader-revenue-private/membership/promotions-tool/DEV/memsub-promotions-keys.conf /etc/gu  --profile membership
   ```

1. Install the frontend dependencies
    ```
    cd frontend
    yarn install
    ```

1. Run ``` ./devrun ``` and navigate to ```https://promo.thegulocal.com```

### Lambdas

   1. Go to lambdas/ folder
   1. Run build.sh
   1. Upload the MembershipSub-Promotions-to-PromoCode-View-Lambda.zip to the Lambda function on S3.
   1. Alternatively, TeamCity will run this same script and build artifact.zip which will be uploaded to riffraff and deployable from there.
   
### Code environment
[https://promo.code.memsub-promotions.gutools.co.uk](https://promo.code.memsub-promotions.gutools.co.uk)
