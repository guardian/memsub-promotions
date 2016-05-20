export default class {

    /* @ngInject */
    constructor($http, environmentService) {
        this.env = environmentService;
        this.$http = $http;
    }

    all() {
        return this.$http({
            method: 'GET',
            url: '/campaigns',
            params: {productFamily: this.env.getProduct()}
        }).then(response => response.data)
    }

    get(code) {
        return this.$http({
            method: 'GET',
            url: '/campaign',
            params: {code: code}
        }).then(response => response.data)
    }

    save(campaign) {
        return this.$http({
            method: 'POST',
            url: '/campaign',
            data: campaign
        }).then(response => response.data)
    }
}