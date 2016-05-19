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

    getStage() {
        return getOrDefault(this.$cookies, STAGE, 'PROD')
    }
}