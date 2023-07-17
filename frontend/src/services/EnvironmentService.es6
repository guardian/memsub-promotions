const CAMPAIGN_GROUP = 'campaign_group';
const DEFAULT_CAMPAIGN_GROUP = 'digitalpack';

function getOrDefault($cookies, key, def) {
    let fromCookies = $cookies.get(key);
    return fromCookies ? fromCookies : def;
}

export default class {

    /* @ngInject */
    constructor($cookies) {
        this.$cookies = $cookies;
    }

    setCampaignGroup(campaignGroup) {
        this.$cookies.put(CAMPAIGN_GROUP, campaignGroup)
    }
    
    getCampaignGroup() {
        return getOrDefault(this.$cookies, CAMPAIGN_GROUP, DEFAULT_CAMPAIGN_GROUP)
    }

    getCampaignGroupPrefix() {
        switch (this.getCampaignGroup()) {
            case 'membership': return 'M';
            case 'digitalpack': return 'D';
            case 'newspaper': return 'N';
            case 'weekly': return 'W';
        }
        return ''
    }

    getGridUrl() {
        return window.guardian.urls['grid'] || '';
    }

    getCampaignGroupDomain() {
        return window.guardian.urls[this.getCampaignGroup()] || '';
    }
}