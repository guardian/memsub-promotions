var path = require('path');

module.exports = {

  entry: './src/index.jsx',

  resolve: {
    extensions: ['*', '.js', '.jsx'],
  },

  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: ['babel-loader'],
      },
    ],
  },

  output: {
    path: __dirname + '/dist',
    filename: 'bundle.js',
    // chunkFilename:  'webpack/[chunkhash].js',
    // filename: "javascripts/[name].js",
    publicPath: '/',
    sourceMapFilename: '[file].map',
  },

  devtool: 'inline-source-map',
  devServer: {
    port: 9501,
    contentBase: './dist',
    disableHostCheck: true,
    headers: {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, PATCH, OPTIONS",
      "Access-Control-Allow-Headers": "X-Requested-With, content-type, Authorization"
    },
    proxy: {
      '**': {
        target: 'http://support.thegulocal.com:9500',
        secure: false,
      },
    },
  },
};
