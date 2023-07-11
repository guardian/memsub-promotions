var path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = {
    plugins: [
        new MiniCssExtractPlugin({
            filename: path.join('stylesheets', '[name].css'),
        })
    ],

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
                use: ['babel-loader'],
            },
            {
                test: /\.scss$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    {
                        loader: 'css-loader',
                    },
                    'sass-loader',
                ],
            }
        ]
    },

    output: {
        path: path.resolve(__dirname, '../public/'),
        chunkFilename:  'webpack/[chunkhash].js',
        filename: "javascripts/[name].js",
        sourceMapFilename: "[file].map",
        publicPath: "/assets/",
    },

    entry: {
        main: path.resolve(__dirname, 'src/main.es6'),
    },

    devtool: 'source-map',

    devServer: {
        proxy: {
            '**': {
                target: 'http://promo.thegulocal.com:9500',
                secure: false,
            },
        },
    },

    stats: {
      errorDetails: true,
   },
};
