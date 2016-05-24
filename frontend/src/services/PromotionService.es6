export default class {

    /* @ngInject */
    constructor($http) {
        this.$http = $http;
    }

    all() {
        return this.$http({
            method: 'GET',
            url: '/promotions'
        }).then(response => response.data)
    }

    get(uuid) {
        return this.$http({
            method: 'GET',
            url: '/promotion',
            params: {uuid: uuid}
        }).then(response => response.data)
    }

    byCampaign(campaignCode) {
        return this.$http({
            method: 'GET',
            url: '/promotions',
            params: {campaignCode: campaignCode}
        }).then(response => response.data)
    }

    save(promotion) {
        return this.$http({
            method: 'POST',
            url: '/promotion',
            data: promotion
        }).then(response => response.data)
    }

    validate(promotion) {
        return this.$http({
            method: 'POST',
            url: '/promotion/validate',
            data: promotion
        }).then(response => response.data)
    }
}