var path = require('path');


module.exports = {

    resolve: {
        root: ["src/", "node_modules/"],
        extensions: ["", ".js", ".es6"],
        alias: {}
    },

    resolveLoader: {
        root: path.join(process.env.PWD, 'node_modules/')
    },

    module: {
        loaders: [
            {
                test: /\.es6$/,
                exclude: /node_modules/,
                loader: 'babel',
                query: {
                    presets: ['es2015'],
                    cacheDirectory: ''
                }
            }
        ]
    },

    progress: true,
    failOnError: true,
    keepalive: false,
    inline: true,
    watch: false,
    hot: false,

    output: {
        path: '../public/',
        chunkFilename:  'webpack/[chunkhash].js',
        filename: "javascripts/[name].js",
        publicPath: '/'
    },

    entry: {
        main: "./src/main"
    },

    stats: {
        modules: true,
        reasons: true,
        colors: true
    },

    context: '.',
    debug: false
};