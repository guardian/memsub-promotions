#!/bin/bash

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

dev-nginx setup-cert promo.thegulocal.com
dev-nginx link-config $DIR/promotions.conf
dev-nginx restart-nginx
