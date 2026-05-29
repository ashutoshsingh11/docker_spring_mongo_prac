// mongo-init.js – runs once when the MongoDB container first starts
db = db.getSiblingDB('cruddb');

db.createUser({
  user: 'cruduser',
  pwd:  'crudpass',
  roles: [{ role: 'readWrite', db: 'cruddb' }]
});

// Seed collection with a sample document (optional)
db.products.insertOne({
  name:        'Sample Laptop',
  description: 'A high-performance laptop',
  price:       999.99,
  quantity:    50,
  category:    'Electronics',
  createdAt:   new Date(),
  updatedAt:   new Date()
});

print('MongoDB initialized: cruddb database and cruduser created.');
