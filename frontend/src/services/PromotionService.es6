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
        return this.all().then(r => r.filter(v => v.uuid == uuid)[0]);
    }

    byCampaign(campaignCode) {
        return this.all().then(p => p.filter(v => v.campaignCode == campaignCode))
    }

    save(promotion) {
        return this.$http({
            method: 'POST',
            url: '/promotion',
            data: promotion
        }).then(response => response.data)
    }

    types() {
        return this.$http({
            method: 'GET',
            url: '/promotion/types'
        }).then(response => response.data)
    }
}