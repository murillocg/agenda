(function() {
    'use strict';

    angular
        .module('agendaApp')
        .controller('AgendamentoDetailController', AgendamentoDetailController);

    AgendamentoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Agendamento', 'Cliente', 'Servico'];

    function AgendamentoDetailController($scope, $rootScope, $stateParams, previousState, entity, Agendamento, Cliente, Servico) {
        var vm = this;

        vm.agendamento = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('agendaApp:agendamentoUpdate', function(event, result) {
            vm.agendamento = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
