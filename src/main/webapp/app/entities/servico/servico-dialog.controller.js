(function() {
    'use strict';

    angular
        .module('agendaApp')
        .controller('ServicoDialogController', ServicoDialogController);

    ServicoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Servico'];

    function ServicoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Servico) {
        var vm = this;

        vm.servico = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.servico.id !== null) {
                Servico.update(vm.servico, onSaveSuccess, onSaveError);
            } else {
                Servico.save(vm.servico, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('agendaApp:servicoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
