const PRODUCT = 'product';
const STAGE = 'stage';
const PRODUCT_DOMAINS = {
    'DEV': {
        'membership': 'mem.thegulocal.com',
        'digitalpack': 'sub.thegulocal.com'
    },
    'PROD': {
        'membership': 'membership.theguardian.com',
        'digitalpack': 'subscribe.theguardian.com'
    },
    'UAT': {
        'membership': 'membership.theguardian.com',
        'digitalpack': 'subscribe.theguardian.com'
    }
};

function getOrDefault($cookies, key, def) {
    let fromCookies = $cookies.get(key);
    return fromCookies ? fromCookies : def;
}

export default class {

    /* @ngInject */
    constructor($cookies) {
        this.$cookies = $cookies;
    }
    
    setStage(newStage) {
        this.$cookies.put(STAGE, newStage)
    }

    setProduct(newProduct) {
        this.$cookies.put(PRODUCT, newProduct)
    }
    
    getProduct() {
        return getOrDefault(this.$cookies, PRODUCT, 'digitalpack')
    }

    getProductPrefix() {
        switch (this.getProduct()) {
            case 'membership': return 'M';
            case 'digitalpack': return 'D';
        }
        return ''
    }

    getProductDomain() {
        return PRODUCT_DOMAINS[this.getStage()][this.getProduct()] || '';
    }

    getStage() {
        return getOrDefault(this.$cookies, STAGE, 'PROD')
    }
}