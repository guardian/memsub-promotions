const PRODUCT = 'product';
const STAGE = 'stage';
const PRODUCT_DOMAINS = {
    'DEV': {
        'membership': 'mem.thegulocal.com',
        'digitalpack': 'sub.thegulocal.com',
        'grid': 'media.test.dev-gutools.co.uk'
    },
    'PROD': {
        'membership': 'membership.theguardian.com',
        'digitalpack': 'subscribe.theguardian.com',
        'grid': 'media.gutools.co.uk'
    },
    'UAT': {
        'membership': 'membership.theguardian.com',
        'digitalpack': 'subscribe.theguardian.com',
        'grid': 'media.gutools.co.uk'
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

    getGridUrl() {
        return PRODUCT_DOMAINS[this.getStage()]['grid'] || '';
    }

    getProductDomain() {
        return PRODUCT_DOMAINS[this.getStage()][this.getProduct()] || '';
    }

    getStage() {
        return getOrDefault(this.$cookies, STAGE, 'PROD')
    }
}