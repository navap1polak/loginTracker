import { OnInit, Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  baseUrl = 'http://localhost:8081';
  webSocketUrl = this.baseUrl + '/websocket';
  getUsersUrl = this.baseUrl + '/api/allUsers';
  client: any;
  users: Array<string> = [];



  ngOnInit() {
    this.http.get<string[]>(this.getUsersUrl).subscribe(
      response => {
        if (response != null && response.length > 0) {
          for (var i = 0; i < response.length; i++)
            this.users.push(response[i]);
        }
     },
        error => {
        console.log(error);
      });
  }

  constructor(private http: HttpClient){
    this.connection();
  }

  connection(){
    let ws = new SockJS(this.webSocketUrl);
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
