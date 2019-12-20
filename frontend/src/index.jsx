import React from 'react';
import ReactDOM from 'react-dom';


console.log('hello');
function render(element) {
  ReactDOM.render(<div>Hello Worlds 2</div>, element);
}

var element = document.querySelector('#content');
//console.log(element);
render(element);
