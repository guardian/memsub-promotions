export default class PromotionService {

    /* @ngInject */
    constructor($http) {
        this.$http = $http;
    }

    all() {
        return this.$http({
            method: 'GET',
            url: '/promotions'
        })
    }
}