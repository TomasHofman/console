$wnd.hal.runAsyncCallback24("function k0e(){j0e()}\nfunction U1e(){T1e()}\nfunction X1e(){W1e()}\nfunction $1e(){Z1e()}\nfunction T1e(){T1e=U6b}\nfunction W1e(){W1e=U6b}\nfunction Z1e(){Z1e=U6b}\nfunction j0e(){j0e=U6b}\nfunction x0e(){x0e=U6b}\nfunction A0e(){A0e=U6b}\nfunction D0e(){D0e=U6b}\nfunction G0e(){G0e=U6b}\nfunction J0e(){J0e=U6b}\nfunction M0e(){M0e=U6b}\nfunction P0e(){P0e=U6b}\nfunction __e(){__e=U6b;rTd()}\nfunction N1e(){N1e=U6b;LRd()}\nfunction m0e(){m0e=U6b;pb();IAg()}\nfunction H0e(a){G0e();this.a=a}\nfunction K0e(a){J0e();this.a=a}\nfunction y0e(a,b){x0e();this.b=a;this.a=b}\nfunction E0e(a,b,d){D0e();this.a=a;this.b=b;this.c=d}\nfunction Q0e(a,b,d,e){P0e();this.b=a;this.d=b;this.c=d;this.a=e}\nfunction B0e(a,b,d,e,g){A0e();this.c=a;this.d=b;this.e=d;this.a=e;this.b=g}\nfunction N0e(a,b,d,e,g){M0e();this.e=a;this.b=b;this.c=d;this.d=e;this.a=g}\nfunction o0e(a,b,d,e){m0e();this.a=a;this.c=b;this.b=d;this.d=e;xb.call(this);this.Reb()}\nfunction X_e(a){U_e();X9d.call(this,a.mj(),a.aQ());this.Leb()}\nfunction f0e(a,b){__e();return new P1e(b,a)}\nfunction i0e(a,b,d,e,g){__e();{JNh(a,lNh(b.X5b().n0b('Mail Session',d)));e.gX(d)}}\nfunction Q1e(a){N1e();return new FBg(wLc('smtp')+' '+'Socket Binding',a.Meb('smtp'))}\nfunction R1e(a){N1e();return new FBg(wLc('imap')+' '+'Socket Binding',a.Meb('imap'))}\nfunction S1e(a){N1e();return new FBg(wLc('pop3')+' '+'Socket Binding',a.Meb('pop3'))}\nfunction g0e(a,b){__e();var d;{d=v4(b.P_('mail-session').I_().bd().pK(new k0e).iK(k9c()),13);a.tj(d)}}\nfunction c0e(a,b,d,e){__e();var g,h;{g=(T_e(),P_e).eUb(a,W2(I2(Rhb,1),{4:1,1:1,5:1,6:1},2,6,[]));h=(new amh('read-resource',g)).ETb('recursive',true).ATb();b.xRb(h,new K0e(e))}}\nfunction h0e(a,b,d,e,g,h,i){__e();var j,k;{if(X4(i)){j=(T_e(),O_e).eUb(a,W2(I2(Rhb,1),{4:1,1:1,5:1,6:1},2,6,[h]));k=(new amh('add',j)).CTb('mail-session',h).FTb(i).ATb();b.xRb(k,new Q0e(d,e,h,g))}}}\nfunction d0e(a,b,d,e,g,h){__e();var i,j;{j=v4(a.WTb((T_e(),O_e)),26);i=new EDg((PCh(),VBh),b.X5b().o0b('Mail Session'),j,SSc(W2(I2(Rhb,1),{4:1,1:1,5:1,6:1},2,6,['jndi-name','from','debug'])),new N0e(d,e,g,b,h));i._x()}}\nfunction b0e(a,b,d,e,g,h,i,j,k){__e();tTd.call(this,(new owg(a,'mail-session','Mail Session')).fGb().eGb());this.Peb();this.qX(new y0e(h,g));this.FW(b.REb((PCh(),SBh),'Mail Session',new B0e(i,k,h,g,e)));this.FW(b.VEb((PCh(),WBh)));this.nX(new E0e(this,d,j));this.sX(new H0e(k))}\nfunction P1e(a,b){N1e();var d;QRd.call(this,a.mj(),a.Neb().Wc()?b.V5b().VZb():b.X5b().A0b(Jg(', ').Ec(a.Neb())).Zt());this.efb();d=new nBg(a);d.IHb('jndi-name');if(a.Oeb('smtp')){d.KHb(new U1e)}if(a.Oeb('imap')){d.KHb(new X1e)}if(a.Oeb('pop3')){d.KHb(new $1e)}d.LHb();this.dW().ZK(d)}\nS6b(703,22,{1:1,12:1,22:1},X_e);_.Meb=function Y_e(a){var b;b=gch(this,'server'+'/'+a+'/'+'outbound-socket-binding-ref');return b.U_()?b.Zt():'n/a'};S6b(2449,34,{1:1,34:1},b0e);_.Peb=function a0e(){};_.Qeb=function e0e(a,b,d){__e();return new o0e(this,d,a,b)};var rEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionColumn',2449,zWb);S6b(2450,1,{1:1},k0e);_.zc=function l0e(a){return new X_e(v4(a,54))};var iEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSession/olumn$0methodref$ctor$Type',2450,Khb);S6b(2456,1,{1:1},o0e);_.Reb=function n0e(){};_.IX=function s0e(){return MAg(this)};_.yl=function t0e(){return NAg(this)};_.JX=function v0e(){return OAg(this)};_.NX=function w0e(){return PAg(this)};_.qS=function p0e(){var a;a=new nSc;a.Pc(this.b.zHb(this.d.$Lb('mail-session').tD('name',this.c.mj()).qD()));a.Pc(this.b.yHb('Mail Session',this.c.mj(),(T_e(),O_e),this.a));return a};_.VL=function q0e(){if(!this.c.Neb().Wc()){return QAg(this.c.mj(),Jg(', ').Ec(this.c.Neb()))}return null};_.HX=function r0e(){var a;a=new nSc;a.Pc(this.c.mj());a.Qc(this.c.Neb());return Ig(32).Ec(a)};_.zl=function u0e(){return this.c.mj()};var jEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionColumn/1',2456,Khb);S6b(2452,1,{1:1},y0e);_.UX=function z0e(a,b){c0e(this.b,this.a,a,b)};var kEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionColumn/lambda$0$Type',2452,Khb);S6b(2455,1,{1:1},B0e);_.WX=function C0e(a){d0e(this.c,this.d,this.e,this.a,this.b,a)};var lEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionColumn/lambda$1$Type',2455,Khb);S6b(2457,1,{1:1},E0e);_.VX=function F0e(a){return this.a.Qeb(this.b,this.c,a)};var mEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionColumn/lambda$2$Type',2457,Khb);S6b(2458,1,{1:1},H0e);_.TX=function I0e(a){return f0e(this.a,a)};var nEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionColumn/lambda$3$Type',2458,Khb);S6b(2451,1,{1:1,24:1},K0e);_.tj=function L0e(a){g0e(this.a,a)};var oEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionColumn/lambda$4$Type',2451,Khb);S6b(2454,1,{1:1},N0e);_.$Y=function O0e(a,b){h0e(this.e,this.b,this.c,this.d,this.a,a,b)};var pEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionColumn/lambda$5$Type',2454,Khb);S6b(2453,1,{1:1,24:1},Q0e);_.tj=function R0e(a){i0e(this.b,this.d,this.c,this.a,a)};var qEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionColumn/lambda$6$Type',2453,Khb);S6b(3059,39,{1:1,14:1,39:1},P1e);_.efb=function O1e(){};var HEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionPreview',3059,sXb);S6b(3060,1,{1:1,145:1},U1e);_.y6=function V1e(a){return Q1e(a)};var EEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionPreview/lambda$0$Type',3060,Khb);S6b(3061,1,{1:1,145:1},X1e);_.y6=function Y1e(a){return R1e(a)};var FEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionPreview/lambda$1$Type',3061,Khb);S6b(3062,1,{1:1,145:1},$1e);_.y6=function _1e(a){return S1e(a)};var GEb=OGc('org.jboss.hal.client.configuration.subsystem.mail','MailSessionPreview/lambda$2$Type',3062,Khb);S6b(1076,1,{1:1});_.pfb=function T2e(){var a;a=this.yfb(this.a.Iz().fIb(),this.a.Iz().dIb(),this.a.Iz().hIb(),this.a.Vy().Ew(),this.a.Tz().BSb(),this.a.Wz()._Ub(),this.a.Wz().$Ub(),this.a.Mz().cMb(),this.a.aA().d6b());this.ufb(a);return a};_.ufb=function Z2e(a){};_.yfb=function b3e(a,b,d,e,g,h,i,j,k){return new b0e(a,b,d,e,g,h,i,j,k)};S6b(1080,1,{61:1,1:1});_.Tj=function v3e(){this.b.tj(this.a.a.pfb())};S6b(115,1,{1:1,124:1});_.VZb=function uxh(){return 'No configured mail servers found.'};S6b(226,1,{1:1,256:1});_.A0b=function oEh(a){return (new Fac).du('Configured mail servers: ').cu(a).eu()};VOh(yH)(24);\n//# sourceURL=hal-24.js\n")