export default class {

    /* @ngInject */
    constructor($http) {
        this.$http = $http;
    }

    all() {
        return this.$http({
            method: 'GET',
            url: '/countries'
        }).then(response => response.data)
    }
}