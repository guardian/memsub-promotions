//views
import table from "text!templates/Promotions.html"

// directives
import promotionList from 'directives/PromotionList'

// services
import PromotionService from "services/PromotionService"

// controllers
import PromotionListController from "controllers/PromotionListController"


let module = angular.module("Promotions", ['ui.router']);

module.service('promotionService', PromotionService)
      .controller('promotionListController', PromotionListController);

module.config(($stateProvider, $urlRouterProvider) => {
   $urlRouterProvider.otherwise("/");

   $stateProvider
       .state('allPromotions', {
           url: "/",
           template: table,
           controller: 'promotionListController',
           controllerAs: 'ctrl'
       })
       .state('singleCampaign', {
           url: "/:code",
           template: table,
           controller: 'promotionListController',
           controllerAs: 'ctrl'
       });
});

module.run(($rootScope, $state, $stateParams) => {
    $rootScope.$stateParams = $stateParams;
});