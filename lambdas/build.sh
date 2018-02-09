#!/usr/bin/env bash
zip MembershipSub-Promotions-to-PromoCode-View-Lambda.zip MembershipSub-Promotions-to-PromoCode-View-Lambda.js
mkdir -p packages/MembershipSub-Promotions-to-PromoCode-View-Lambda
cp MembershipSub-Promotions-to-PromoCode-View-Lambda.zip packages/MembershipSub-Promotions-to-PromoCode-View-Lambda
zip -r MembershipSub-Promotions-to-PromoCode-View-Lambda.zip.zip packages riff-raff.yaml
echo "##teamcity[publishArtifacts '$(pwd)/MembershipSub-Promotions-to-PromoCode-View-Lambda.zip']"
