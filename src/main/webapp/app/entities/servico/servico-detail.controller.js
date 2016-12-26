(function() {
    'use strict';

    angular
        .module('agendaApp')
        .controller('ServicoDetailController', ServicoDetailController);

    ServicoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Servico'];

    function ServicoDetailController($scope, $rootScope, $stateParams, previousState, entity, Servico) {
        var vm = this;

        vm.servico = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('agendaApp:servicoUpdate', function(event, result) {
            vm.servico = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
