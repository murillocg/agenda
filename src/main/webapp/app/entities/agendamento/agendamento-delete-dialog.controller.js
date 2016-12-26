(function() {
    'use strict';

    angular
        .module('agendaApp')
        .controller('AgendamentoDeleteController',AgendamentoDeleteController);

    AgendamentoDeleteController.$inject = ['$uibModalInstance', 'entity', 'Agendamento'];

    function AgendamentoDeleteController($uibModalInstance, entity, Agendamento) {
        var vm = this;

        vm.agendamento = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Agendamento.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
