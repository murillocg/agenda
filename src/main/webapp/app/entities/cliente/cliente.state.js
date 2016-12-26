(function() {
    'use strict';

    angular
        .module('agendaApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('cliente', {
            parent: 'entity',
            url: '/cliente',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'agendaApp.cliente.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cliente/clientes.html',
                    controller: 'ClienteController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cliente');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('cliente-detail', {
            parent: 'entity',
            url: '/cliente/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'agendaApp.cliente.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cliente/cliente-detail.html',
                    controller: 'ClienteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cliente');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Cliente', function($stateParams, Cliente) {
                    return Cliente.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'cliente',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('cliente-detail.edit', {
            parent: 'cliente-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cliente/cliente-dialog.html',
                    controller: 'ClienteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Cliente', function(Cliente) {
                            return Cliente.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('cliente.new', {
            parent: 'cliente',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cliente/cliente-dialog.html',
                    controller: 'ClienteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nome: null,
                                telefone: null,
                                email: null,
                                dataCadastro: new Date(),
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('cliente', null, { reload: 'cliente' });
                }, function() {
                    $state.go('cliente');
                });
            }]
        })
        .state('cliente.edit', {
            parent: 'cliente',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cliente/cliente-dialog.html',
                    controller: 'ClienteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Cliente', function(Cliente) {
                            return Cliente.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('cliente', null, { reload: 'cliente' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('cliente.delete', {
            parent: 'cliente',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cliente/cliente-delete-dialog.html',
                    controller: 'ClienteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Cliente', function(Cliente) {
                            return Cliente.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('cliente', null, { reload: 'cliente' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
