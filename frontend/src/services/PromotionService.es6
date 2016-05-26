export default class {

    /* @ngInject */
    constructor($http, $q) {
        this.$http = $http;
        this.$q = $q;
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

    invalidPromotion(errors) {
        let allErrors = {};
        Object.keys(errors).forEach(originalPath => {
            let normalisedPath = originalPath.substring(4, originalPath.length);
            let normalisedErrors = {};
            errors[originalPath].forEach(e => normalisedErrors[e.msg[0]] = true);
            allErrors[normalisedPath] = normalisedErrors;
        });
        return this.$q.reject(allErrors);
    }

    validate(promotion) {
        return this.$http({
            method: 'POST',
            url: '/promotion/validate',
            data: promotion
        }).then(r => {
            let data = r.data;
            if (data.status && data.status === 'ok') {
                return this.$q.resolve(promotion);
            } else {
                return this.invalidPromotion(data);
            }
        })
    }
}