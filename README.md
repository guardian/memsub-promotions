Memsubity promotions
====================

### NGinx

   To run standalone you can use the default nginx installation as follows:

   Install nginx:

   Mac OSX: `brew install nginx`

   Make sure you have a sites-enabled folder under your nginx home. This should be

   Mac OSX: `~/Developers/etc/nginx/sites-enabled` or `/usr/local/etc/nginx/`
   Make sure your nginx.conf (found in your nginx home) contains the following line in the `http{...}` block: `include sites-enabled/*`;

   Run: `./nginx/setup.sh`

   ## General Setup

   1. Go to project root
   1. Add the following to your hosts file in `/etc/hosts`

      ```
      127.0.0.1   promo.thegulocal.com
      ```

   1. Change the ownership of the 'gu' directory under 'etc' to current user.
      `$ sudo -i chown -R {username} /etc/gu`
      
   1. Run `./nginx/setup.sh`

   1. Setup AWS credentials with [Janus](https://janus.gutools.co.uk/).

      In `~/.aws/config` add the following:

      ```
      [default]
      region = eu-west-1
      ```

   1. Run ``` sbt devrun ``` and navigate to ```promo.thegulocal.com```

### Lambdas

   1. Go to lambdas/ folder
   1. Run build.sh
   1. Upload the MembershipSub-Promotions-to-PromoCode-View-Lambda.zip to the Lambda function on S3.
   1. Alternatively, TeamCity will run this same script and build artifact.zip which will be uploaded to riffraff and deployable from there.
