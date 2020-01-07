var path = require('path');


module.exports = {

    resolve: {
        extensions: [".js", ".es6"],
        modules: [
            path.resolve(__dirname, 'src'),
            path.resolve(__dirname, 'node_modules'),
        ],
    },

    resolveLoader: {
        alias: {
            text: 'text-loader'
        }
    },

    module: {
        rules: [
            {
                test: /\.es6$/,
                exclude: /node_modules/,
                use: ['babel-loader', 'ng-annotate-loader'],
            }
        ]
    },

    output: {
        path: path.resolve(__dirname, '../public/'),
        chunkFilename:  'webpack/[chunkhash].js',
        filename: "javascripts/[name].js",
        sourceMapFilename: "[file].map"
    },

    entry: {
        main: path.resolve(__dirname, 'src/main.es6'),
    },

    devtool: 'none',

    devServer: {
        contentBase: path.resolve(__dirname, '../public'),
        publicPath: '/assets/',
        disableHostCheck: true,
        proxy: {
            '**': {
                target: 'http://promo.thegulocal.com:9500',
                secure: false,
            },
        },
    }
};
