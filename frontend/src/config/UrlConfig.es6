/* @ngInject */
export default ($sceDelegateProvider) => {
    $sceDelegateProvider.resourceUrlWhitelist([
        'self', 'https://media.test.dev-gutools.co.uk**', 'https://media.gutools.co.uk**'
    ]);
}