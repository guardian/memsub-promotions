/* @ngInject */
export default ($mdDateLocaleProvider) => {
    $mdDateLocaleProvider.formatDate = date => date ? date.toLocaleDateString('en-GB') : ''
}