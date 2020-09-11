import { OnInit, Component } from '@angular/core';
import { Title } from '@angular/platform-browser';

import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import $ from 'jquery';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  url = 'http://localhost:8081/websocket'
  client: any;
  users: Array<string> = [];

  ngOnInit() {
    this.title.setTitle('Angular Spring Websocket');
  }

  constructor(private title: Title){
    this.connection();
  }

  connection(){
    let ws = new SockJS(this.url);
    this.client = Stomp.over(ws);
    let that = this;

    this.client.connect({}, function(frame) {
      that.client.subscribe("/topic/logins", (message) => {
        if(message.body) {
         that.users.push(message.body);
        }
      });
    });
  }
}
