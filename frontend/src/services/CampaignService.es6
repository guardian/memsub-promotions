export default class {

    /* @ngInject */
    constructor($http) {
        this.$http = $http;
    }

    all() {
        return this.$http({
            method: 'GET',
            url: '/campaigns'
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