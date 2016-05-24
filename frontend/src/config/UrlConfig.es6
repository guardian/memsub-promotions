/* @ngInject */
export default ($sceDelegateProvider) => {
    $sceDelegateProvider.resourceUrlWhitelist([
        'self', 'https://media.test.dev-gutools.co.uk**', 'https://media.gutools.co.uk**',
        'https://mem.thegulocal.com**', 'https://membership.theguardian.com**',
        'https://sub.thegulocal.com**', 'https://subscribe.theguardian.com**'
    ]);
}