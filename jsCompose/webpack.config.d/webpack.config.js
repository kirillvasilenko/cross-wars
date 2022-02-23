// wrap is useful, because declaring variables in module can be already declared
// module creates own lexical environment
;(function (config) {
    const serverHost = 'localhost:8080'
    config.devServer = config.devServer || {}
    config.devServer.port = "8090"
    config.devServer.historyApiFallback = true
    config.devServer.proxy = {
        '/api/ws': {
            target: 'ws://' + serverHost,
            ws: true
        },
        '/api': {
            target: 'http://' + serverHost,
            secure: false
        }
    }
})(config);