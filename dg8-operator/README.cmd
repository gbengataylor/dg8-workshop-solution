```
oc apply -f cr_minimal.yaml

#get the load balancer
URL=oc get services | grep datagrid-service-external | awk '{ print $4 }'

#access URL at
http://$URL:11222

#get credentials using
oc get secret datagrid-service-generated-secret -o jsonpath="{.data.identities\.yaml}" | base64 --decode

```