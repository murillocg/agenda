(function() {
    'use strict';

    angular
        .module('agendaApp')
        .controller('AgendamentoDialogController', AgendamentoDialogController);

    AgendamentoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Agendamento', 'Cliente', 'Servico'];

    function AgendamentoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Agendamento, Cliente, Servico) {
        var vm = this;

        vm.agendamento = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.clientes = Cliente.query();
        vm.servicos = Servico.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.agendamento.id !== null) {
                Agendamento.update(vm.agendamento, onSaveSuccess, onSaveError);
            } else {
                Agendamento.save(vm.agendamento, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('agendaApp:agendamentoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.dataHoraInicio = false;
        vm.datePickerOpenStatus.dataHoraFim = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
