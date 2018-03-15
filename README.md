Memsubity promotions
====================

### NGinx

#### First, setup Nginx for `Identity-Platform`

Set up Nginx config and SSL certs from Identity. Follow the README
[here](https://github.com/guardian/identity-platform/blob/master/README.md#setup-nginx-for-local-development)
first, before you do anything else but make sure to pass the profile 'membership' to the script:

Within `identity-platform`, run:
   ```
   sudo -E nginx/setup.sh membership --with-jdk-import
   ```
and enter your password where prompted.

#### Then, run the Nginx setup script specific to `memsub-promotions`

Run the `memsub-promotions`-specific [setup.sh](nginx/setup.sh) script from the root
of the `memsub-promotions` project:

```
./nginx/setup.sh
```

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
   aws s3 cp s3://gu-promotions-tool-private/DEV/memsub-promotions-keys.conf /etc/gu  --profile membership
   ```

1. Install and compile the frontend resources
    ```
    cd frontend
    yarn install
    yarn dev
    ```

1. Run ``` sbt devrun ``` and navigate to ```promo.thegulocal.com```

### Lambdas

   1. Go to lambdas/ folder
   1. Run build.sh
   1. Upload the MembershipSub-Promotions-to-PromoCode-View-Lambda.zip to the Lambda function on S3.
   1. Alternatively, TeamCity will run this same script and build artifact.zip which will be uploaded to riffraff and deployable from there.
