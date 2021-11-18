define(["exports","./when-54c2dc71","./Check-6c0211bc","./Math-fc8cecf5","./Cartesian2-bddc1162","./Transforms-43808565","./ComponentDatatype-6d99a1ee","./GeometryAttribute-49698167","./GeometryAttributes-4fcfcf40","./IndexDatatype-53503fee","./GeometryOffsetAttribute-7350d9af","./VertexFormat-7572c785","./CylinderGeometryLibrary-b0214ab1"],function(t,I,e,U,S,B,Y,Z,J,W,j,u,q){"use strict";var H=new S.Cartesian2,K=new S.Cartesian3,Q=new S.Cartesian3,X=new S.Cartesian3,$=new S.Cartesian3;function d(t){var e=(t=I.defaultValue(t,I.defaultValue.EMPTY_OBJECT)).length,a=t.topRadius,r=t.bottomRadius,n=I.defaultValue(t.vertexFormat,u.VertexFormat.DEFAULT),o=I.defaultValue(t.slices,128);this._length=e,this._topRadius=a,this._bottomRadius=r,this._vertexFormat=u.VertexFormat.clone(n),this._slices=o,this._offsetAttribute=t.offsetAttribute,this._workerName="createCylinderGeometry"}d.packedLength=u.VertexFormat.packedLength+5,d.pack=function(t,e,a){return a=I.defaultValue(a,0),u.VertexFormat.pack(t._vertexFormat,e,a),a+=u.VertexFormat.packedLength,e[a++]=t._length,e[a++]=t._topRadius,e[a++]=t._bottomRadius,e[a++]=t._slices,e[a]=I.defaultValue(t._offsetAttribute,-1),e};var a,f=new u.VertexFormat,p={vertexFormat:f,length:void 0,topRadius:void 0,bottomRadius:void 0,slices:void 0,offsetAttribute:void 0};d.unpack=function(t,e,a){e=I.defaultValue(e,0);var r=u.VertexFormat.unpack(t,e,f);e+=u.VertexFormat.packedLength;var n=t[e++],o=t[e++],i=t[e++],s=t[e++],m=t[e];return I.defined(a)?(a._vertexFormat=u.VertexFormat.clone(r,a._vertexFormat),a._length=n,a._topRadius=o,a._bottomRadius=i,a._slices=s,a._offsetAttribute=-1===m?void 0:m,a):(p.length=n,p.topRadius=o,p.bottomRadius=i,p.slices=s,p.offsetAttribute=-1===m?void 0:m,new d(p))},d.createGeometry=function(t){var e=t._length,a=t._topRadius,r=t._bottomRadius,n=t._vertexFormat,o=t._slices;if(!(e<=0||a<0||r<0||0===a&&0===r)){var i=o+o,s=o+i,m=i+i,u=q.CylinderGeometryLibrary.computePositions(e,a,r,o,!0),d=n.st?new Float32Array(2*m):void 0,f=n.normal?new Float32Array(3*m):void 0,p=n.tangent?new Float32Array(3*m):void 0,y=n.bitangent?new Float32Array(3*m):void 0,l=n.normal||n.tangent||n.bitangent;if(l){var c=n.tangent||n.bitangent,b=0,v=0,A=0,g=Math.atan2(r-a,e),h=K;h.z=Math.sin(g);for(var x=Math.cos(g),_=X,C=Q,F=0;F<o;F++){var w=F/o*U.CesiumMath.TWO_PI,G=x*Math.cos(w),D=x*Math.sin(w);l&&(h.x=G,h.y=D,c&&(_=S.Cartesian3.normalize(S.Cartesian3.cross(S.Cartesian3.UNIT_Z,h,_),_)),n.normal&&(f[b++]=h.x,f[b++]=h.y,f[b++]=h.z,f[b++]=h.x,f[b++]=h.y,f[b++]=h.z),n.tangent&&(p[v++]=_.x,p[v++]=_.y,p[v++]=_.z,p[v++]=_.x,p[v++]=_.y,p[v++]=_.z),n.bitangent&&(C=S.Cartesian3.normalize(S.Cartesian3.cross(h,_,C),C),y[A++]=C.x,y[A++]=C.y,y[A++]=C.z,y[A++]=C.x,y[A++]=C.y,y[A++]=C.z))}for(F=0;F<o;F++)n.normal&&(f[b++]=0,f[b++]=0,f[b++]=-1),n.tangent&&(p[v++]=1,p[v++]=0,p[v++]=0),n.bitangent&&(y[A++]=0,y[A++]=-1,y[A++]=0);for(F=0;F<o;F++)n.normal&&(f[b++]=0,f[b++]=0,f[b++]=1),n.tangent&&(p[v++]=1,p[v++]=0,p[v++]=0),n.bitangent&&(y[A++]=0,y[A++]=1,y[A++]=0)}var R=12*o-12,V=W.IndexDatatype.createTypedArray(m,R),T=0,O=0;for(F=0;F<o-1;F++)V[T++]=O,V[T++]=O+2,V[T++]=O+3,V[T++]=O,V[T++]=O+3,V[T++]=O+1,O+=2;for(V[T++]=i-2,V[T++]=0,V[T++]=1,V[T++]=i-2,V[T++]=1,V[T++]=i-1,F=1;F<o-1;F++)V[T++]=i+F+1,V[T++]=i+F,V[T++]=i;for(F=1;F<o-1;F++)V[T++]=s,V[T++]=s+F,V[T++]=s+F+1;var L=0;if(n.st){var P=Math.max(a,r);for(F=0;F<m;F++){var k=S.Cartesian3.fromArray(u,3*F,$);d[L++]=(k.x+P)/(2*P),d[L++]=(k.y+P)/(2*P)}}var M=new J.GeometryAttributes;n.position&&(M.position=new Z.GeometryAttribute({componentDatatype:Y.ComponentDatatype.DOUBLE,componentsPerAttribute:3,values:u})),n.normal&&(M.normal=new Z.GeometryAttribute({componentDatatype:Y.ComponentDatatype.FLOAT,componentsPerAttribute:3,values:f})),n.tangent&&(M.tangent=new Z.GeometryAttribute({componentDatatype:Y.ComponentDatatype.FLOAT,componentsPerAttribute:3,values:p})),n.bitangent&&(M.bitangent=new Z.GeometryAttribute({componentDatatype:Y.ComponentDatatype.FLOAT,componentsPerAttribute:3,values:y})),n.st&&(M.st=new Z.GeometryAttribute({componentDatatype:Y.ComponentDatatype.FLOAT,componentsPerAttribute:2,values:d})),H.x=.5*e,H.y=Math.max(r,a);var z,E,N=new B.BoundingSphere(S.Cartesian3.ZERO,S.Cartesian2.magnitude(H));return I.defined(t._offsetAttribute)&&(e=u.length,z=new Uint8Array(e/3),E=t._offsetAttribute===j.GeometryOffsetAttribute.NONE?0:1,j.arrayFill(z,E),M.applyOffset=new Z.GeometryAttribute({componentDatatype:Y.ComponentDatatype.UNSIGNED_BYTE,componentsPerAttribute:1,values:z})),new Z.Geometry({attributes:M,indices:V,primitiveType:Z.PrimitiveType.TRIANGLES,boundingSphere:N,offsetAttribute:t._offsetAttribute})}},d.getUnitCylinder=function(){return I.defined(a)||(a=d.createGeometry(new d({topRadius:1,bottomRadius:1,length:1,vertexFormat:u.VertexFormat.POSITION_ONLY}))),a},t.CylinderGeometry=d});
