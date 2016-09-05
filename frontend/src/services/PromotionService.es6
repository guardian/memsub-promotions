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

    validate(promotion) {
        if (promotion.promotionType.a.name == promotion.promotionType.b.name) {
            return this.$q.reject(["Both promotions cannot be of the same type."])
        }
        return this.$http({
            method: 'POST',
            url: '/promotion/validate',
            data: promotion
        }).then(r => {
            let data = r.data;
            if (data.status && data.status === 'ok') {
                return this.$q.resolve(promotion);
            } else {
                return this.$q.reject(Object.keys(data).map(f => 
                    f + ": " + data[f].map(e => e.msg.join(" ")).join(" ")
                ));
            }
        }, e => {
            return this.$q.reject([e.status + " " + e.statusText])
        })
    }
}