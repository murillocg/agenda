(function() {
    'use strict';

    angular
        .module('agendaApp')
        .controller('ServicoDeleteController',ServicoDeleteController);

    ServicoDeleteController.$inject = ['$uibModalInstance', 'entity', 'Servico'];

    function ServicoDeleteController($uibModalInstance, entity, Servico) {
        var vm = this;

        vm.servico = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Servico.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
