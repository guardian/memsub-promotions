const PRODUCT = 'product';
const STAGE = 'stage';

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
        switch (this.getProduct()) {
            case 'membership': return 'membership.theguardian.com';
            case 'digitalpack': return 'subscribe.theguardian.com';
        }
        return ''
    }

    getStage() {
        return getOrDefault(this.$cookies, STAGE, 'PROD')
    }
}