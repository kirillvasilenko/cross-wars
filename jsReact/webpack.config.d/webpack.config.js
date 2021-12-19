// wrap is useful, because declaring variables in module can be already declared
// module creates own lexical environment
;(function (config) {
    const serverUrl = 'http://localhost:8080'
    config.devServer = config.devServer || {}
    config.devServer.port = "8090"
    config.devServer.proxy = {
        '/api': {
            target: serverUrl,
            secure: false,
        }
    }
})(config);