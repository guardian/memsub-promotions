# Subscription promotions tool

## URLs
[CODE](https://promo.code.memsub-promotions.gutools.co.uk)

[PROD](https://memsub-promotions.gutools.co.uk/)

[DEV](https://promo.thegulocal.com/)

## Running

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

OR

```
sbt devrun
cd frontend; yarn devrun
```


## Initial Setup

### NGinx
1. `cd nginx`
1. Install dependencies `brew bundle`
2. Run `./setup.sh`

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

### Lambdas

See the [lambdas/](lambdas/) folder
   

