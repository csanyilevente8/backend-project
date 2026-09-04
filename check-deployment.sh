# watch pods roll in real time
kubectl get pods -n todo -l app=backend -w

# see the rollout history (each image change is a revision)
kubectl rollout history deployment/backend -n todo

# see the ReplicaSets — old one scales to 0, new one to 1
kubectl get rs -n todo -l app=backend

# describe the deployment to see the rolling-update strategy + events
kubectl describe deployment backend -n todo

