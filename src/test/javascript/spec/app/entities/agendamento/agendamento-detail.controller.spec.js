'use strict';

describe('Controller Tests', function() {

    describe('Agendamento Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockAgendamento, MockCliente, MockServico;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockAgendamento = jasmine.createSpy('MockAgendamento');
            MockCliente = jasmine.createSpy('MockCliente');
            MockServico = jasmine.createSpy('MockServico');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Agendamento': MockAgendamento,
                'Cliente': MockCliente,
                'Servico': MockServico
            };
            createController = function() {
                $injector.get('$controller')("AgendamentoDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'agendaApp:agendamentoUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
