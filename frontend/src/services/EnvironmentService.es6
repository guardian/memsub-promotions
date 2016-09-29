const CAMPAIGN_GROUP = 'campaign_group';
const DEFAULT_CAMPAIGN_GROUP = 'digitalpack';
const STAGE = 'stage';
const DEFAULT_STAGE = 'PROD';   // Must match the default router in AppLoader.scala
const PRODUCT_DOMAINS = {
    'DEV': {
        'membership': 'mem.thegulocal.com',
        'digitalpack': 'sub.thegulocal.com',
        'newspaper': 'sub.thegulocal.com',
        'weekly': 'sub.thegulocal.com',
        'grid': 'media.gutools.co.uk'
    },
    'PROD': {
        'membership': 'membership.theguardian.com',
        'digitalpack': 'subscribe.theguardian.com',
        'newspaper': 'subscribe.theguardian.com',
        'weekly': 'subscribe.theguardian.com',
        'grid': 'media.gutools.co.uk'
    },
    'UAT': {
        'membership': 'membership.theguardian.com',
        'digitalpack': 'subscribe.theguardian.com',
        'newspaper': 'subscribe.theguardian.com',
        'weekly': 'subscribe.theguardian.com',
        'grid': 'media.gutools.co.uk'
    }
};

function getOrDefault($cookies, key, def) {
    let fromCookies = $cookies.get(key);
    return fromCookies ? fromCookies : def;
}

export default class {

    /* @ngInject */
    constructor($cookies) {
        this.$cookies = $cookies;
    }
    
    setStage(newStage) {
        this.$cookies.put(STAGE, newStage)
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
        return PRODUCT_DOMAINS[this.getStage()]['grid'] || '';
    }

    getCampaignGroupDomain() {
        return PRODUCT_DOMAINS[this.getStage()][this.getCampaignGroup()] || '';
    }

    getStage() {
        return getOrDefault(this.$cookies, STAGE, DEFAULT_STAGE)
    }
}