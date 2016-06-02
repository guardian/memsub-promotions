#!/usr/bin/env bash
zip MembershipSub-Promotions-to-PromoCode-View-Lambda.zip MembershipSub-Promotions-to-PromoCode-View-Lambda.js
mkdir -p packages/MembershipSub-Promotions-to-PromoCode-View-Lambda
cp MembershipSub-Promotions-to-PromoCode-View-Lambda.zip packages/MembershipSub-Promotions-to-PromoCode-View-Lambda
zip -r artifacts.zip packages deploy.json
echo "##teamcity[publishArtifacts '$(pwd)/artifacts.zip']"
