export default class {

    /* @ngInject */
    constructor($http, $q) {
        this.$http = $http;
        this.$q = $q;
    }

    sortChannelKeysAndPromoCodes(promotion) {
        const channelKeys = Array.sort(Object.keys(promotion.codes));
        const codes = {};
        channelKeys.forEach(channel => {
            codes[channel] = Array.sort(promotion.codes[channel]); // Hack! Also sorts the object keys!
        });
        return Object.assign({},promotion,{codes:codes});
    }

    all() {
        return this.$http({
            method: 'GET',
            url: '/promotions'
        })
        .then(response => response.data)
        .then(promotions => promotions.map(this.sortChannelKeysAndPromoCodes));
    }

    get(uuid) {
        return this.$http({
            method: 'GET',
            url: '/promotion',
            params: {uuid: uuid}
        }).then(response => this.sortChannelKeysAndPromoCodes(response.data))
    }

    byCampaign(campaignCode) {
        return this.$http({
            method: 'GET',
            url: '/promotions',
            params: {campaignCode: campaignCode}
        })
        .then(response => response.data)
        .then(promotions => promotions.map(this.sortChannelKeysAndPromoCodes));
    }

    save(promotion) {
        return this.$http({
            method: 'POST',
            url: '/promotion',
            data: this.sortChannelKeysAndPromoCodes(promotion)
        }).then(response => response.data)
    }

    validate(promotion) {
        if (promotion.promotionType.a && promotion.promotionType.b && promotion.promotionType.a.name == promotion.promotionType.b.name) {
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