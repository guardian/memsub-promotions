#!/usr/bin/env bash
cp MembershipSub-Promotions-to-PromoCode-View-Lambda.es6 MembershipSub-Promotions-to-PromoCode-View-Lambda.js
zip -m MembershipSub-Promotions-to-PromoCode-View-Lambda.zip MembershipSub-Promotions-to-PromoCode-View-Lambda.js
mkdir -p packages/MembershipSub-Promotions-to-PromoCode-View-Lambda
mv MembershipSub-Promotions-to-PromoCode-View-Lambda.zip packages/MembershipSub-Promotions-to-PromoCode-View-Lambda
zip -r artifacts.zip packages deploy.json
echo "##teamcity[publishArtifacts '$(pwd)/artifacts.zip']"