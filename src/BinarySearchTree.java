import java.util.List;

public class BinarySearchTree<T extends RowData> {
    public TreeNode<T> root;
    public long size;

    public BinarySearchTree() {
        this.size = 0;
    }

    public TreeNode generateBST(List<T> dataList) {
        BinarySearchTree tree = new BinarySearchTree();
        TreeNode tmp = tree.root;
        if (dataList.isEmpty()) {
            return tmp;
        }

        tmp = insertNode(tmp, dataList.get(0));
        for (int i = 1; i < dataList.size(); i++) {
            insertNode(tmp, dataList.get(i));
        }
        return tmp;
    }

    public TreeNode<T> insertNode(TreeNode node, T row) {

        if (node == null) {
            node = new TreeNode<>(row);
            ++size;
            return node;
        }

        if (row.compareTo(node.value) <= 0 && null != node.left) {
            insertNode(node.left, row);
        } else {
            if (row.compareTo(node.value) <= 0) {
                node.insertLeft(row);
            } else if (row.compareTo(node.value) > 0 && null != node.left) {
                insertNode(node.right, row);
            } else {
                node.insertRight(row);
            }
        }
        return null;
    }


    public RowData findNode(TreeNode node, ColumnValue value) {
        if (value.compareTo(node.value.key) < 0 && null != node.left) {
            return findNode(node.left, value);
        } else if (value.compareTo(node.value.key) < 0) {
            return null;
        } else if (value.compareTo(node.value.key) > 0 && null != node.right) {
            return findNode(node.right, value);
        } else if (value.compareTo(node.value.key) > 0) {
            return null;
        } else {
            return node.value;
        }
    }


    public boolean deleteNode(TreeNode node, ColumnValue value, TreeNode parent) {
        if (value.compareTo(node.value.key) < 0 && null != node.left) {
            return deleteNode(node.left, value, node);
        } else if (value.compareTo(node.value.key) < 0) {
            return false;
        } else if (value.compareTo(node.value.key) > 0 && node.right != null) {
            return deleteNode(node.right, value, node);
        } else if (value.compareTo(node.value.key) > 0) {
            return false;
        } else {
            if (node.left == null && node.right == null) {
                if (node == parent.left) {
                    parent.left = null;
                    --size;
                    return true;
                } else {
                    parent.right = null;
                    --size;
                    return true;
                }
            } else if (node.left != null && node.right == null) {
                if (node == parent.left) {
                    parent.left = node.left;
                    --size;
                    return true;
                } else {
                    parent.right = node.left;
                    --size;
                    return true;
                }
            } else if (node.left == null && node.right != null) {
                if (node == parent.left) {
                    parent.left = node.right;
                    --size;
                    return true;
                } else {
                    parent.right = node.right;
                    --size;
                    return true;
                }
            } else {
                RowData minValue = findMinValue(node.right);
                node.value = minValue;
                deleteNode(node.right, minValue.key, node);
                --size;
                return true;
            }
        }
    }

    public void inOrder(TreeNode node, List<RowData> result) {
        if (node.left != null) {
            inOrder(node.left, result);
        }
        result.add(node.value);
        if (node.right != null) {
            inOrder(node.right, result);
        }
    }


    private RowData findMinValue(TreeNode node) {
        if (node == null) {
            return null;
        }
        if (node.left == null) {
            return node.value;
        } else {
            return findMinValue(node.left);
        }
    }
}